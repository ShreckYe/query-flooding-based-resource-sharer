package shreckye.qfbresourcesharer.net.resource.client

import shreckye.qfbresourcesharer.data.protocol.resource.ResourceMessageFactoryParser
import shreckye.qfbresourcesharer.data.protocol.resource.request.ResourceRequest
import shreckye.qfbresourcesharer.data.protocol.resource.response.ResourceResponse
import shreckye.qfbresourcesharer.net.resource.ResourceMessenger
import java.net.Socket

class ResourceClientMessenger(socket: Socket) : ResourceMessenger<ResourceRequest<*>, ResourceResponse<*>>(socket) {
    override val parser: ResourceMessageFactoryParser<ResourceResponse<*>> = ResourceResponse
}