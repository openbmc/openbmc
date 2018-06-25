IMAGE_PREPROCESS_COMMAND_append = " combine_dbg_image; "

combine_dbg_image () {
        if [ "${IMAGE_GEN_DEBUGFS}" = "1" -a -e ${IMAGE_ROOTFS}-dbg ]; then
                # copy target files into -dbg rootfs, so it can be used for
                # debug purposes directly
                tar -C ${IMAGE_ROOTFS} -cf - . | tar -C ${IMAGE_ROOTFS}-dbg -xf -
        fi
}
