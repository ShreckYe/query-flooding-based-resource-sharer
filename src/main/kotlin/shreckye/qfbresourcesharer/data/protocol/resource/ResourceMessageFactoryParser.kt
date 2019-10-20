package shreckye.qfbresourcesharer.data.protocol.resource

import shreckye.qfbresourcesharer.data.protocol.ProtocolMessageFactoryParser

interface ResourceMessageFactoryParser<RM : ResourceMessage<*>> : ProtocolMessageFactoryParser<RM>