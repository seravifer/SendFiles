package sendFiles.util

import org.chris.portmapper.PortMapperApp
import org.chris.portmapper.model.PortMapping
import org.chris.portmapper.model.Protocol
import org.chris.portmapper.router.AbstractRouterFactory
import org.chris.portmapper.router.IRouter
import org.chris.portmapper.router.RouterException
import org.chris.portmapper.router.cling.ClingRouterFactory
import tornadofx.FX.Companion.log

/**
 * Work in progress...
 */
fun main(args: Array<String>) {
    val router = connect()

    if (router != null) {
        if (!existMapping(router)) addPortForwarding(router)
        router.disconnect()
    }
}

@Throws(RouterException::class)
private fun connect(): IRouter? {
    val routerFactory: AbstractRouterFactory
    try {
        routerFactory = createRouterFactory()
    } catch (e: RouterException) {
        log.warning("Could not create router factory")
        return null
    }
    return selectRouter(routerFactory.findRouters())
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
}

private fun existMapping(router: IRouter) = router.portMappings.any { it.description ==  "OpenPort"}

private fun existPort(router: IRouter) = router.portMappings.any { it.internalPort ==  4444 || it.externalPort == 4444 }

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
    if (foundRouters.isNotEmpty()) {
        val router = foundRouters[0]
        log.info("Conected to " + router.name)
        return router
    } else {
        log.warning("Found no router")
        return null
    }
}