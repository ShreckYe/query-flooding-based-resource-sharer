package shreckye.qfbresourcesharer.net.resource.server

import shreckye.qfbresourcesharer.data.protocol.resource.ResourceMessageFactoryParser
import shreckye.qfbresourcesharer.data.protocol.resource.request.ResourceRequest
import shreckye.qfbresourcesharer.data.protocol.resource.response.ResourceResponse
import shreckye.qfbresourcesharer.net.resource.ResourceMessenger
import java.net.Socket

class ResourceServerMessenger(socket: Socket) : ResourceMessenger<ResourceResponse<*>, ResourceRequest<*>>(socket) {
    override val parser: ResourceMessageFactoryParser<ResourceRequest<*>> = ResourceRequest
}