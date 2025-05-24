PACKAGECONFIG:append:rpi = " gallium vc4 v3d ${@bb.utils.contains('DISTRO_FEATURES', 'vulkan', 'vulkan broadcom', '', d)}"
