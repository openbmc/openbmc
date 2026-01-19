# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

inherit kernel

copy_initramfs() {
        echo "Copying initramfs into ./usr ..."
        # In case the directory is not created yet from the first pass compile:
        mkdir -p ${B}/usr
        # Find and use the first initramfs image archive type we find
        rm -f ${B}/usr/${INITRAMFS_IMAGE_NAME}.cpio.xz
        if [ -e "${INITRAMFS_DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE_NAME}.cpio.xz" ]; then
                 cp ${INITRAMFS_DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE_NAME}.cpio.xz ${B}/usr/.
        fi
}


do_bundle_initramfs () {
        if [ ! -z "${INITRAMFS_IMAGE}" -a x"${INITRAMFS_IMAGE_BUNDLE}" = x1 ]; then
                echo "Creating a kernel image with a bundled initramfs..."
                copy_initramfs
                # Backing up kernel image relies on its type(regular file or symbolic link)
                tmp_path=""
                for imageType in ${KERNEL_IMAGETYPE_FOR_MAKE} ; do
                        if [ -h ${KERNEL_OUTPUT_DIR}/$imageType ] ; then
                                linkpath=`readlink -n ${KERNEL_OUTPUT_DIR}/$imageType`
                                realpath=`readlink -fn ${KERNEL_OUTPUT_DIR}/$imageType`
                                mv -f $realpath $realpath.bak
                                tmp_path=$tmp_path" "$imageType"#"$linkpath"#"$realpath
                        elif [ -f ${KERNEL_OUTPUT_DIR}/$imageType ]; then
                                mv -f ${KERNEL_OUTPUT_DIR}/$imageType ${KERNEL_OUTPUT_DIR}/$imageType.bak
                                tmp_path=$tmp_path" "$imageType"##"
                        fi
                done
                use_alternate_initrd=CONFIG_INITRAMFS_SOURCE=${B}/usr/${INITRAMFS_IMAGE_NAME}.cpio.xz
                kernel_do_compile
                # Restoring kernel image
                for tp in $tmp_path ; do
                        imageType=`echo $tp|cut -d "#" -f 1`
                        linkpath=`echo $tp|cut -d "#" -f 2`
                        realpath=`echo $tp|cut -d "#" -f 3`
                        if [ -n "$realpath" ]; then
                                mv -f $realpath $realpath.initramfs
                                mv -f $realpath.bak $realpath
                                ln -sf $linkpath.initramfs ${B}/${KERNEL_OUTPUT_DIR}/$imageType.initramfs
                        else
                                mv -f ${KERNEL_OUTPUT_DIR}/$imageType ${KERNEL_OUTPUT_DIR}/$imageType.initramfs
                                mv -f ${KERNEL_OUTPUT_DIR}/$imageType.bak ${KERNEL_OUTPUT_DIR}/$imageType
                        fi
                done
        fi
}
