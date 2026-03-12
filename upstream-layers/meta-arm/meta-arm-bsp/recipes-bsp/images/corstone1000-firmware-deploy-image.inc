COMPATIBLE_MACHINE = "corstone1000"

FIRMWARE_BINARIES = "corstone1000-flash-firmware-image-${MACHINE}.wic \
                     trusted-firmware-m/bl1.bin \
                     ${@bb.utils.contains('MACHINE_FEATURES', \
                                          'corstone1000-extsys', \
                                          'es_flashfw.bin', \
                                          '', d)} \
                     ${CAPSULE_NAME}.${CAPSULE_EXTENSION} \
                     corstone1000_capsule_cert.crt \
                     corstone1000_capsule_key.key \
                    "

do_deploy[mcdepends] = "mc::firmware:corstone1000-flash-firmware-image:do_image_complete"
