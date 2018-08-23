OpenBMC Evaluation Board (EVB)
================

This is the OpenBMC Evaluation Board (EVB) layer. The boards in the EVB layer
are for evaluation and reference. The [Board Support Package (BSP)
layer](https://github.com/openbmc/openbmc/tree/master/)
should be used to support a hardware device in a system. Therefore, there
should not be any EVB layers in a system's bblayers.conf, the BSP layer should
be used instead. Boards might be found be in both the EVB layer and the BSP
layer.
