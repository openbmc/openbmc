inherit uboot-config

CONVERSIONTYPES += "fitImage"

CONVERSION_CMD:fitImage = "run_assemble_fitimage ${IMAGE_NAME}.${type}"
INITRAMFS_IMAGE="${IMAGE_NAME}.cpio.${INITRAMFS_CTYPE}"
KERNEL_OUTPUT_DIR="${DEPLOY_DIR_IMAGE}"

FIT_KERNEL_COMP_ALG ?= "none"
FIT_KERNEL_COMP_ALG_EXTENSION ?= ""

do_image_cpio[depends] += "virtual/kernel:do_deploy"

run_assemble_fitimage() {
    export linux_comp="${FIT_KERNEL_COMP_ALG}"
    fitimage_assemble $1.its $1.fitImage 1

    # The fitimage_assemble puts the image into DEPLOY_DIR_NAME due to
    # KERNEL_OUTPUT_DIR, but we really want it still in ${IMGDEPLOYDIR}, so
    # move it.
    mv ${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE}.fitImage .
    # Delete the spurious linux.bin created by our stubbed uboot_prep_kimage.
    rm linux.bin
}

UBOOT_MKIMAGE_KERNEL_TYPE ?= "kernel"
uboot_prep_kimage() {
    cp ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} linux.bin

    if [ "${linux_comp}" != "none" ] ; then
        linux_suffix="${FIT_KERNEL_COMP_ALG_EXTENSION}"
        if [ "${linux_comp}" = "gzip" ] ; then
            gzip -9 linux.bin
        elif [ "${linux_comp}" = "lzo" ] ; then
            lzop -9 linux.bin
        fi
        mv -f "linux.bin${linux_suffix}" linux.bin
    fi
}

DEPENDS:append = " u-boot-tools-native dtc-native virtual/cross-binutils"

# Description string
FIT_DESC ?= "Kernel fitImage for ${DISTRO_NAME}/${PV}/${MACHINE}"

# Kernel fitImage Hash Algo
FIT_HASH_ALG ?= "sha256"

# Kernel fitImage Signature Algo
FIT_SIGN_ALG ?= "rsa2048"

# Kernel / U-Boot fitImage Padding Algo
FIT_PAD_ALG ?= "pkcs-1.5"

# Generate keys for signing Kernel fitImage
FIT_GENERATE_KEYS ?= "0"

# Size of private keys in number of bits
FIT_SIGN_NUMBITS ?= "2048"

# args to openssl genrsa (Default is just the public exponent)
FIT_KEY_GENRSA_ARGS ?= "-F4"

# args to openssl req (Default is -batch for non interactive mode and
# -new for new certificate)
FIT_KEY_REQ_ARGS ?= "-batch -new"

# Standard format for public key certificate
FIT_KEY_SIGN_PKCS ?= "-x509"

# Sign individual images as well
FIT_SIGN_INDIVIDUAL ?= "0"

FIT_CONF_PREFIX ?= "conf-"
FIT_CONF_PREFIX[doc] = "Prefix to use for FIT configuration node name"

FIT_SUPPORTED_INITRAMFS_FSTYPES ?= "cpio.lz4 cpio.lzo cpio.lzma cpio.xz cpio.zst cpio.gz ext2.gz cpio"

#
# Emit the fitImage ITS header
#
# $1 ... .its filename
fitimage_emit_fit_header() {
    cat << EOF >> $1
/dts-v1/;

/ {
        description = "${FIT_DESC}";
        #address-cells = <1>;
EOF
}

#
# Emit the fitImage section bits
#
# $1 ... .its filename
# $2 ... Section bit type: imagestart - image section start
#                          confstart  - configuration section start
#                          sectend    - section end
#                          fitend     - fitimage end
#
fitimage_emit_section_maint() {
    case $2 in
    imagestart)
        cat << EOF >> $1

        images {
EOF
    ;;
    confstart)
        cat << EOF >> $1

        configurations {
EOF
    ;;
    sectend)
        cat << EOF >> $1
    };
EOF
    ;;
    fitend)
        cat << EOF >> $1
};
EOF
    ;;
    esac
}


#
# Emit the fitImage ITS kernel section
#
# $1 ... .its filename
# $2 ... Image counter
# $3 ... Path to kernel image
# $4 ... Compression type
fitimage_emit_section_kernel() {

    kernel_csum="${FIT_HASH_ALG}"
    kernel_sign_algo="${FIT_SIGN_ALG}"
    kernel_sign_keyname="${UBOOT_SIGN_IMG_KEYNAME}"

    ENTRYPOINT="${UBOOT_ENTRYPOINT}"
    if [ -n "${UBOOT_ENTRYSYMBOL}" ]; then
        ENTRYPOINT=`${HOST_PREFIX}nm vmlinux | \
            awk '$3=="${UBOOT_ENTRYSYMBOL}" {print "0x"$1;exit}'`
    fi

    cat << EOF >> $1
                kernel-$2 {
                        description = "Linux kernel";
                        data = /incbin/("$3");
                        type = "${UBOOT_MKIMAGE_KERNEL_TYPE}";
                        arch = "${UBOOT_ARCH}";
                        os = "linux";
                        compression = "$4";
                        load = <${UBOOT_LOADADDRESS}>;
                        entry = <$ENTRYPOINT>;
                        hash-1 {
                                algo = "$kernel_csum";
                        };
                };
EOF

    if [ "${UBOOT_SIGN_ENABLE}" = "1" -a "${FIT_SIGN_INDIVIDUAL}" = "1" -a -n "$kernel_sign_keyname" ] ; then
        sed -i '$ d' $1
        cat << EOF >> $1
                        signature-1 {
                                algo = "$kernel_csum,$kernel_sign_algo";
                                key-name-hint = "$kernel_sign_keyname";
                        };
                };
EOF
    fi
}

#
# Emit the fitImage ITS DTB section
#
# $1 ... .its filename
# $2 ... Image counter
# $3 ... Path to DTB image
fitimage_emit_section_dtb() {

    dtb_csum="${FIT_HASH_ALG}"
    dtb_sign_algo="${FIT_SIGN_ALG}"
    dtb_sign_keyname="${UBOOT_SIGN_IMG_KEYNAME}"

    dtb_loadline=""
    dtb_ext=${DTB##*.}
    if [ "${dtb_ext}" = "dtbo" ]; then
        if [ -n "${UBOOT_DTBO_LOADADDRESS}" ]; then
            dtb_loadline="load = <${UBOOT_DTBO_LOADADDRESS}>;"
        fi
    elif [ -n "${UBOOT_DTB_LOADADDRESS}" ]; then
        dtb_loadline="load = <${UBOOT_DTB_LOADADDRESS}>;"
    fi
    cat << EOF >> $1
                fdt-$2 {
                        description = "Flattened Device Tree blob";
                        data = /incbin/("$3");
                        type = "flat_dt";
                        arch = "${UBOOT_ARCH}";
                        compression = "none";
                        $dtb_loadline
                        hash-1 {
                                algo = "$dtb_csum";
                        };
                };
EOF

    if [ "${UBOOT_SIGN_ENABLE}" = "1" -a "${FIT_SIGN_INDIVIDUAL}" = "1" -a -n "$dtb_sign_keyname" ] ; then
        sed -i '$ d' $1
        cat << EOF >> $1
                        signature-1 {
                                algo = "$dtb_csum,$dtb_sign_algo";
                                key-name-hint = "$dtb_sign_keyname";
                        };
                };
EOF
    fi
}

#
# Emit the fitImage ITS u-boot script section
#
# $1 ... .its filename
# $2 ... Image counter
# $3 ... Path to boot script image
fitimage_emit_section_boot_script() {

    bootscr_csum="${FIT_HASH_ALG}"
    bootscr_sign_algo="${FIT_SIGN_ALG}"
    bootscr_sign_keyname="${UBOOT_SIGN_IMG_KEYNAME}"

        cat << EOF >> $1
                bootscr-$2 {
                        description = "U-boot script";
                        data = /incbin/("$3");
                        type = "script";
                        arch = "${UBOOT_ARCH}";
                        compression = "none";
                        hash-1 {
                                algo = "$bootscr_csum";
                        };
                };
EOF

    if [ "${UBOOT_SIGN_ENABLE}" = "1" -a "${FIT_SIGN_INDIVIDUAL}" = "1" -a -n "$bootscr_sign_keyname" ] ; then
        sed -i '$ d' $1
        cat << EOF >> $1
                        signature-1 {
                                algo = "$bootscr_csum,$bootscr_sign_algo";
                                key-name-hint = "$bootscr_sign_keyname";
                        };
                };
EOF
    fi
}

#
# Emit the fitImage ITS setup section
#
# $1 ... .its filename
# $2 ... Image counter
# $3 ... Path to setup image
fitimage_emit_section_setup() {

    setup_csum="${FIT_HASH_ALG}"

    cat << EOF >> $1
                setup-$2 {
                        description = "Linux setup.bin";
                        data = /incbin/("$3");
                        type = "x86_setup";
                        arch = "${UBOOT_ARCH}";
                        os = "linux";
                        compression = "none";
                        load = <0x00090000>;
                        entry = <0x00090000>;
                        hash-1 {
                                algo = "$setup_csum";
                        };
                };
EOF
}

#
# Emit the fitImage ITS ramdisk section
#
# $1 ... .its filename
# $2 ... Image counter
# $3 ... Path to ramdisk image
fitimage_emit_section_ramdisk() {

    ramdisk_csum="${FIT_HASH_ALG}"
    ramdisk_sign_algo="${FIT_SIGN_ALG}"
    ramdisk_sign_keyname="${UBOOT_SIGN_IMG_KEYNAME}"
    ramdisk_loadline=""
    ramdisk_entryline=""

    if [ -n "${UBOOT_RD_LOADADDRESS}" ]; then
        ramdisk_loadline="load = <${UBOOT_RD_LOADADDRESS}>;"
    fi
    if [ -n "${UBOOT_RD_ENTRYPOINT}" ]; then
        ramdisk_entryline="entry = <${UBOOT_RD_ENTRYPOINT}>;"
    fi

    cat << EOF >> $1
                ramdisk-$2 {
                        description = "${INITRAMFS_IMAGE}";
                        data = /incbin/("$3");
                        type = "ramdisk";
                        arch = "${UBOOT_ARCH}";
                        os = "linux";
                        compression = "none";
                        $ramdisk_loadline
                        $ramdisk_entryline
                        hash-1 {
                                algo = "$ramdisk_csum";
                        };
                };
EOF

    if [ "${UBOOT_SIGN_ENABLE}" = "1" -a "${FIT_SIGN_INDIVIDUAL}" = "1" -a -n "$ramdisk_sign_keyname" ] ; then
        sed -i '$ d' $1
        cat << EOF >> $1
                        signature-1 {
                                algo = "$ramdisk_csum,$ramdisk_sign_algo";
                                key-name-hint = "$ramdisk_sign_keyname";
                        };
                };
EOF
    fi
}

#
# echoes symlink destination if it points below directory
#
# $1 ... file that's a potential symlink
# $2 ... expected parent directory
symlink_points_below() {
    file="$2/$1"
    dir=$2

    if ! [ -L "$file" ]; then
        return
    fi

    realpath="$(realpath --relative-to=$dir $file)"
    if [ -z "${realpath%%../*}" ]; then
        return
    fi

    echo "$realpath"
}

#
# Emit the fitImage ITS configuration section
#
# $1 ... .its filename
# $2 ... Linux kernel ID
# $3 ... DTB image name
# $4 ... ramdisk ID
# $5 ... u-boot script ID
# $6 ... config ID
# $7 ... default flag
fitimage_emit_section_config() {

    conf_csum="${FIT_HASH_ALG}"
    conf_sign_algo="${FIT_SIGN_ALG}"
    conf_padding_algo="${FIT_PAD_ALG}"
    if [ "${UBOOT_SIGN_ENABLE}" = "1" ] ; then
        conf_sign_keyname="${UBOOT_SIGN_KEYNAME}"
    fi

    its_file="$1"
    kernel_id="$2"
    dtb_image="$3"
    ramdisk_id="$4"
    bootscr_id="$5"
    config_id="$6"
    default_flag="$7"

    # Test if we have any DTBs at all
    sep=""
    conf_desc=""
    conf_node="${FIT_CONF_PREFIX}"
    kernel_line=""
    fdt_line=""
    ramdisk_line=""
    bootscr_line=""
    setup_line=""
    default_line=""

    dtb_image_sect=$(symlink_points_below $dtb_image "${EXTERNAL_KERNEL_DEVICETREE}")
    if [ -z "$dtb_image_sect" ]; then
        dtb_image_sect=$dtb_image
    fi

    dtb_image=$(echo $dtb_image | tr '/' '_')
    dtb_image_sect=$(echo "${dtb_image_sect}" | tr '/' '_')

    # conf node name is selected based on dtb ID if it is present,
    # otherwise its selected based on kernel ID
    if [ -n "$dtb_image" ]; then
        conf_node=$conf_node$dtb_image
    else
        conf_node=$conf_node$kernel_id
    fi

    if [ -n "$kernel_id" ]; then
        conf_desc="Linux kernel"
        sep=", "
        kernel_line="kernel = \"kernel-$kernel_id\";"
    fi

    if [ -n "$dtb_image" ]; then
        conf_desc="$conf_desc${sep}FDT blob"
        sep=", "
        fdt_line="fdt = \"fdt-$dtb_image_sect\";"
    fi

    if [ -n "$ramdisk_id" ]; then
        conf_desc="$conf_desc${sep}ramdisk"
        sep=", "
        ramdisk_line="ramdisk = \"ramdisk-$ramdisk_id\";"
    fi

    if [ -n "$bootscr_id" ]; then
        conf_desc="$conf_desc${sep}u-boot script"
        sep=", "
        bootscr_line="bootscr = \"bootscr-$bootscr_id\";"
    fi

    if [ -n "$config_id" ]; then
        conf_desc="$conf_desc${sep}setup"
        setup_line="setup = \"setup-$config_id\";"
    fi

    if [ "$default_flag" = "1" ]; then
        # default node is selected based on dtb ID if it is present,
        # otherwise its selected based on kernel ID
        if [ -n "$dtb_image" ]; then
            default_line="default = \"${FIT_CONF_PREFIX}$dtb_image\";"
        else
            default_line="default = \"${FIT_CONF_PREFIX}$kernel_id\";"
        fi
    fi

    cat << EOF >> $its_file
                $default_line
                $conf_node {
                        description = "$default_flag $conf_desc";
                        $kernel_line
                        $fdt_line
                        $ramdisk_line
                        $bootscr_line
                        $setup_line
                        hash-1 {
                                algo = "$conf_csum";
                        };
EOF

    if [ -n "$conf_sign_keyname" ] ; then

        sign_line="sign-images = "
        sep=""

        if [ -n "$kernel_id" ]; then
            sign_line="$sign_line${sep}\"kernel\""
            sep=", "
        fi

        if [ -n "$dtb_image" ]; then
            sign_line="$sign_line${sep}\"fdt\""
            sep=", "
        fi

        if [ -n "$ramdisk_id" ]; then
            sign_line="$sign_line${sep}\"ramdisk\""
            sep=", "
        fi

        if [ -n "$bootscr_id" ]; then
            sign_line="$sign_line${sep}\"bootscr\""
            sep=", "
        fi

        if [ -n "$config_id" ]; then
            sign_line="$sign_line${sep}\"setup\""
        fi

        sign_line="$sign_line;"

        cat << EOF >> $its_file
                        signature-1 {
                                algo = "$conf_csum,$conf_sign_algo";
                                key-name-hint = "$conf_sign_keyname";
                                padding = "$conf_padding_algo";
                                $sign_line
                        };
EOF
    fi

    cat << EOF >> $its_file
                };
EOF
}

#
# Assemble fitImage
#
# $1 ... .its filename
# $2 ... fitImage name
# $3 ... include ramdisk
fitimage_assemble() {
    kernelcount=1
    dtbcount=""
    DTBS=""
    ramdiskcount=$3
    setupcount=""
    bootscr_id=""
    rm -f $1 ${KERNEL_OUTPUT_DIR}/$2

    if [ -n "${UBOOT_SIGN_IMG_KEYNAME}" -a "${UBOOT_SIGN_KEYNAME}" = "${UBOOT_SIGN_IMG_KEYNAME}" ]; then
        bbfatal "Keys used to sign images and configuration nodes must be different."
    fi

    fitimage_emit_fit_header $1

    #
    # Step 1: Prepare a kernel image section.
    #
    fitimage_emit_section_maint $1 imagestart

    uboot_prep_kimage
    fitimage_emit_section_kernel $1 $kernelcount linux.bin "$linux_comp"

    #
    # Step 2: Prepare a DTB image section
    #

    if [ -n "${KERNEL_DEVICETREE}" ]; then
        dtbcount=1
        for DTB in ${KERNEL_DEVICETREE}; do
            if echo $DTB | grep -q '/dts/'; then
                bbwarn "$DTB contains the full path to the the dts file, but only the dtb name should be used."
                DTB=`basename $DTB | sed 's,\.dts$,.dtb,g'`
            fi

            # Skip ${DTB} if it's also provided in ${EXTERNAL_KERNEL_DEVICETREE}
            if [ -n "${EXTERNAL_KERNEL_DEVICETREE}" ] && [ -s ${EXTERNAL_KERNEL_DEVICETREE}/${DTB} ]; then
                continue
            fi

            # For non-vendored DTBs, we need to strip off the vendor path.
            if "${@'false' if oe.types.boolean(d.getVar('KERNEL_DTBVENDORED')) else 'true'}"; then
                DTB=`basename $DTB`
            fi

            DTB_PATH="${KERNEL_OUTPUT_DIR}/dts/$DTB"
            if [ ! -e "$DTB_PATH" ]; then
                DTB_PATH="${KERNEL_OUTPUT_DIR}/$DTB"
            fi

            # Skip DTB if we've picked it up previously
            echo "$DTBS" | tr ' ' '\n' | grep -xq "$DTB" && continue

            DTBS="$DTBS $DTB"
            DTB=$(echo $DTB | tr '/' '_')
            fitimage_emit_section_dtb $1 $DTB $DTB_PATH
        done
    fi

    if [ -n "${EXTERNAL_KERNEL_DEVICETREE}" ]; then
        dtbcount=1
        for DTB in $(find "${EXTERNAL_KERNEL_DEVICETREE}" \( -name '*.dtb' -o -name '*.dtbo' \) -printf '%P\n' | sort); do
            # Skip DTB if we've picked it up previously
            echo "$DTBS" | tr ' ' '\n' | grep -xq "$DTB" && continue

            DTBS="$DTBS $DTB"

            # Also skip if a symlink. We'll later have each config section point at it
            [ $(symlink_points_below $DTB "${EXTERNAL_KERNEL_DEVICETREE}") ] && continue

            DTB=$(echo $DTB | tr '/' '_')
            fitimage_emit_section_dtb $1 $DTB "${EXTERNAL_KERNEL_DEVICETREE}/$DTB"
        done
    fi

    #
    # Step 3: Prepare a u-boot script section
    #

    if [ -n "${UBOOT_ENV}" ] && [ -d "${STAGING_DIR_HOST}/boot" ]; then
        if [ -e "${STAGING_DIR_HOST}/boot/${UBOOT_ENV_BINARY}" ]; then
            cp ${STAGING_DIR_HOST}/boot/${UBOOT_ENV_BINARY} ${B}
            bootscr_id="${UBOOT_ENV_BINARY}"
            fitimage_emit_section_boot_script $1 "$bootscr_id" ${UBOOT_ENV_BINARY}
        else
            bbwarn "${STAGING_DIR_HOST}/boot/${UBOOT_ENV_BINARY} not found."
        fi
    fi

    #
    # Step 4: Prepare a setup section. (For x86)
    #
    if [ -e ${KERNEL_OUTPUT_DIR}/setup.bin ]; then
        setupcount=1
        fitimage_emit_section_setup $1 $setupcount ${KERNEL_OUTPUT_DIR}/setup.bin
    fi

    #
    # Step 5: Prepare a ramdisk section.
    #
    if [ "x${ramdiskcount}" = "x1" ] && [ "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
        # Find and use the first initramfs image archive type we find
        found=
        for img in ${FIT_SUPPORTED_INITRAMFS_FSTYPES}; do
            initramfs_path="${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE_NAME}.$img"
            if [ -e "$initramfs_path" ]; then
                bbnote "Found initramfs image: $initramfs_path"
                found=true
                fitimage_emit_section_ramdisk $1 "$ramdiskcount" "$initramfs_path"
                break
            else
                bbnote "Did not find initramfs image: $initramfs_path"
            fi
        done

        if [ -z "$found" ] && [ -e ${INITRAMFS_IMAGE} ]; then
            found=true
            bbnote "Found initramfs image: ${INITRAMFS_IMAGE}"
            fitimage_emit_section_ramdisk $1 "$ramdiskcount" "${INITRAMFS_IMAGE}"
        fi

        if [ -z "$found" ]; then
            bbfatal "Could not find a valid initramfs type for ${INITRAMFS_IMAGE_NAME}, the supported types are: ${FIT_SUPPORTED_INITRAMFS_FSTYPES}"
        fi
    fi

    fitimage_emit_section_maint $1 sectend

    # Force the first Kernel and DTB in the default config
    kernelcount=1
    if [ -n "$dtbcount" ]; then
        dtbcount=1
    fi

    #
    # Step 6: Prepare a configurations section
    #
    fitimage_emit_section_maint $1 confstart

    # kernel-fitimage.bbclass currently only supports a single kernel (no less or
    # more) to be added to the FIT image along with 0 or more device trees and
    # 0 or 1 ramdisk.
        # It is also possible to include an initramfs bundle (kernel and rootfs in one binary)
        # When the initramfs bundle is used ramdisk is disabled.
    # If a device tree is to be part of the FIT image, then select
    # the default configuration to be used is based on the dtbcount. If there is
    # no dtb present than select the default configuation to be based on
    # the kernelcount.
    if [ -n "$DTBS" ]; then
        i=1
        for DTB in ${DTBS}; do
            dtb_ext=${DTB##*.}
            if [ "$dtb_ext" = "dtbo" ]; then
                fitimage_emit_section_config $1 "" "$DTB" "" "$bootscr_id" "" "`expr $i = $dtbcount`"
            else
                fitimage_emit_section_config $1 $kernelcount "$DTB" "$ramdiskcount" "$bootscr_id" "$setupcount" "`expr $i = $dtbcount`"
            fi
            i=`expr $i + 1`
        done
    else
        defaultconfigcount=1
        fitimage_emit_section_config $1 $kernelcount "" "$ramdiskcount" "$bootscr_id"  "$setupcount" $defaultconfigcount
    fi

    fitimage_emit_section_maint $1 sectend

    fitimage_emit_section_maint $1 fitend

    #
    # Step 7: Assemble the image
    #
    ${UBOOT_MKIMAGE} \
        ${@'-D "${UBOOT_MKIMAGE_DTCOPTS}"' if len('${UBOOT_MKIMAGE_DTCOPTS}') else ''} \
        -f $1 \
        ${KERNEL_OUTPUT_DIR}/$2

    #
    # Step 8: Sign the image
    #
    if [ "x${UBOOT_SIGN_ENABLE}" = "x1" ] ; then
        ${UBOOT_MKIMAGE_SIGN} \
            ${@'-D "${UBOOT_MKIMAGE_DTCOPTS}"' if len('${UBOOT_MKIMAGE_DTCOPTS}') else ''} \
            -F -k "${UBOOT_SIGN_KEYDIR}" \
            -r ${KERNEL_OUTPUT_DIR}/$2 \
            ${UBOOT_MKIMAGE_SIGN_ARGS}
    fi
}
