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
private fun connect(): IRouter? =
    try { selectRouter(createRouterFactory().findRouters()) }
    catch (e: RouterException) {
        log.warning("Could not create router factory")
        null
    }


@Throws(RouterException::class)
private fun addPortForwarding(router: IRouter) {
    val internalClient = localIP.hostAddress
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
    val routerFactoryClass = Class.forName(ClingRouterFactory::class.java.name) as? Class<AbstractRouterFactory>
            ?: throw RouterException("Did not find router factory class for name ${ClingRouterFactory::class.java.name}")

    try {
        val constructor = routerFactoryClass.getConstructor(PortMapperApp::class.java)
        return constructor.newInstance(PortMapperApp())
    } catch (e: Exception) {
        throw RouterException("Error creating a router factory using class " + routerFactoryClass.name, e)
    }
}

private fun selectRouter(foundRouters: List<IRouter>): IRouter? =
    foundRouters.firstOrNull()
            .also { if (it == null) log.warning("Router not found") else log.info("Connected to ${it.name}") }
