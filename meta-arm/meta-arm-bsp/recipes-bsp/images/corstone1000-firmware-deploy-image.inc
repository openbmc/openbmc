COMPATIBLE_MACHINE = "corstone1000"

FIRMWARE_BINARIES = "corstone1000-flash-firmware-image-${MACHINE}.wic \
                     bl1.bin \
                     es_flashfw.bin \
                     ${CAPSULE_NAME}.${CAPSULE_EXTENSION} \
                     corstone1000_capsule_cert.crt \
                     corstone1000_capsule_key.key \
                    "

do_deploy[mcdepends] = "mc::firmware:corstone1000-flash-firmware-image:do_image_complete"
