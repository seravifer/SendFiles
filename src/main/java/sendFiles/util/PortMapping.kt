package sendFiles.util

import org.chris.portmapper.PortMapperApp
import org.chris.portmapper.model.PortMapping
import org.chris.portmapper.model.Protocol
import org.chris.portmapper.router.AbstractRouterFactory
import org.chris.portmapper.router.IRouter
import org.chris.portmapper.router.RouterException
import org.chris.portmapper.router.cling.ClingRouterFactory
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Inet4Address

/**
 * Work in progress
 */
fun main(args: Array<String>) {
    var router = connect()
    if (router != null) {
        addPortForwarding(router)
        router.disconnect()
    }
}

/**
 * Solve a problem that confuses ip from VMs
 * And problem on Linux with non-loopback IP
 */
private fun getLocalIP(): InetAddress? {
    val en = NetworkInterface.getNetworkInterfaces()
    while (en.hasMoreElements()) {
        val en2 = en.nextElement().inetAddresses
        while (en2.hasMoreElements()) {
            val addr = en2.nextElement()
            if (!addr.isLoopbackAddress && addr is Inet4Address) {
                return addr
            }
        }
    }
    return null
}

@Throws(RouterException::class)
private fun connect(): IRouter? {
    val routerFactory: AbstractRouterFactory
    try {
        routerFactory = createRouterFactory()
    } catch (e: RouterException) {
        println("Could not create router factory")
        return null
    }
    val foundRouters = routerFactory.findRouters()
    return selectRouter(foundRouters)
}

@Throws(RouterException::class)
private fun addPortForwarding(router: IRouter) {
    val internalClient = getLocalIP()?.hostAddress
    val internalPort = 4444
    val externalPort = 4444
    val protocol = Protocol.TCP
    val description = "OpenPort"
    val mapping = PortMapping(protocol, null, externalPort, internalClient, internalPort, description)

    router.addPortMapping(mapping)
    printPortForwardings(router)
}

@Throws(RouterException::class)
private fun printPortForwardings(router: IRouter) {
    val mappings = router.portMappings
    if (mappings.isEmpty()) {
        println("No port mappings found")
        return
    }
    val b = StringBuilder()
    val iterator = mappings.iterator()
    while (iterator.hasNext()) {
        val mapping = iterator.next()
        b.append(mapping.completeDescription)
        if (iterator.hasNext()) {
            b.append("\n")
        }
    }
    println("Found " + mappings.size + " port forwardings:\n" + b.toString())
}

@Throws(RouterException::class)
private fun createRouterFactory(): AbstractRouterFactory {
    val routerFactoryClass: Class<AbstractRouterFactory>
    try {
        routerFactoryClass = Class.forName(ClingRouterFactory::class.java.name) as Class<AbstractRouterFactory>
    } catch (e: ClassNotFoundException) {
        throw RouterException("Did not find router factory class for name " + ClingRouterFactory::class.java.name, e)
    }

    try {
        val constructor = routerFactoryClass.getConstructor(PortMapperApp::class.java)
        return constructor.newInstance(PortMapperApp())
    } catch (e: Exception) {
        throw RouterException("Error creating a router factory using class " + routerFactoryClass.name, e)
    }

}

private fun selectRouter(foundRouters: List<IRouter>): IRouter? {
    if (foundRouters.size == 1) {
        val router = foundRouters.iterator().next()
        println("Connected to router " + router.name)
        return router
    } else if (foundRouters.isEmpty()) {
        println("Found no router")
        return null
    } else if (foundRouters.size > 1) {
        val router = foundRouters[1]
        println("Found more than one router, using " + router.name)
        return router
    } else {
        println("Found no router")
        return null
    }
}