# This file is part of U-Boot verified boot support and is intended to be
# inherited from u-boot recipe and from kernel-fitimage.bbclass.
#
# The signature procedure requires the user to generate an RSA key and
# certificate in a directory and to define the following variable:
#
#   UBOOT_SIGN_KEYDIR = "/keys/directory"
#   UBOOT_SIGN_KEYNAME = "dev" # keys name in keydir (eg. "dev.crt", "dev.key")
#   UBOOT_MKIMAGE_DTCOPTS = "-I dts -O dtb -p 2000"
#   UBOOT_SIGN_ENABLE = "1"
#
# As verified boot depends on fitImage generation, following is also required:
#
#   KERNEL_CLASSES ?= " kernel-fitimage "
#   KERNEL_IMAGETYPE ?= "fitImage"
#
# The signature support is limited to the use of CONFIG_OF_SEPARATE in U-Boot.
#
# The tasks sequence is set as below, using DEPLOY_IMAGE_DIR as common place to
# treat the device tree blob:
#
# * u-boot:do_install:append
#   Install UBOOT_DTB_BINARY to datadir, so that kernel can use it for
#   signing, and kernel will deploy UBOOT_DTB_BINARY after signs it.
#
# * virtual/kernel:do_assemble_fitimage
#   Sign the image
#
# * u-boot:do_deploy[postfuncs]
#   Deploy files like UBOOT_DTB_IMAGE, UBOOT_DTB_SYMLINK and others.
#
# For more details on signature process, please refer to U-Boot documentation.

# We need some variables from u-boot-config
inherit uboot-config

# Enable use of a U-Boot fitImage
UBOOT_FITIMAGE_ENABLE ?= "0"

# Signature activation - these require their respective fitImages
UBOOT_SIGN_ENABLE ?= "0"
SPL_SIGN_ENABLE ?= "0"

# Default value for deployment filenames.
UBOOT_DTB_IMAGE ?= "u-boot-${MACHINE}-${PV}-${PR}.dtb"
UBOOT_DTB_BINARY ?= "u-boot.dtb"
UBOOT_DTB_SYMLINK ?= "u-boot-${MACHINE}.dtb"
UBOOT_NODTB_IMAGE ?= "u-boot-nodtb-${MACHINE}-${PV}-${PR}.bin"
UBOOT_NODTB_BINARY ?= "u-boot-nodtb.bin"
UBOOT_NODTB_SYMLINK ?= "u-boot-nodtb-${MACHINE}.bin"
UBOOT_ITS_IMAGE ?= "u-boot-its-${MACHINE}-${PV}-${PR}"
UBOOT_ITS ?= "u-boot.its"
UBOOT_ITS_SYMLINK ?= "u-boot-its-${MACHINE}"
UBOOT_FITIMAGE_IMAGE ?= "u-boot-fitImage-${MACHINE}-${PV}-${PR}"
UBOOT_FITIMAGE_BINARY ?= "u-boot-fitImage"
UBOOT_FITIMAGE_SYMLINK ?= "u-boot-fitImage-${MACHINE}"
SPL_DIR ?= "spl"
SPL_DTB_IMAGE ?= "u-boot-spl-${MACHINE}-${PV}-${PR}.dtb"
SPL_DTB_BINARY ?= "u-boot-spl.dtb"
SPL_DTB_SYMLINK ?= "u-boot-spl-${MACHINE}.dtb"
SPL_NODTB_IMAGE ?= "u-boot-spl-nodtb-${MACHINE}-${PV}-${PR}.bin"
SPL_NODTB_BINARY ?= "u-boot-spl-nodtb.bin"
SPL_NODTB_SYMLINK ?= "u-boot-spl-nodtb-${MACHINE}.bin"

# U-Boot fitImage description
UBOOT_FIT_DESC ?= "U-Boot fitImage for ${DISTRO_NAME}/${PV}/${MACHINE}"

# Kernel / U-Boot fitImage Hash Algo
FIT_HASH_ALG ?= "sha256"
UBOOT_FIT_HASH_ALG ?= "sha256"

# Kernel / U-Boot fitImage Signature Algo
FIT_SIGN_ALG ?= "rsa2048"
UBOOT_FIT_SIGN_ALG ?= "rsa2048"

# Kernel / U-Boot fitImage Padding Algo
FIT_PAD_ALG ?= "pkcs-1.5"

# Generate keys for signing Kernel / U-Boot fitImage
FIT_GENERATE_KEYS ?= "0"
UBOOT_FIT_GENERATE_KEYS ?= "0"

# Size of private keys in number of bits
FIT_SIGN_NUMBITS ?= "2048"
UBOOT_FIT_SIGN_NUMBITS ?= "2048"

# args to openssl genrsa (Default is just the public exponent)
FIT_KEY_GENRSA_ARGS ?= "-F4"
UBOOT_FIT_KEY_GENRSA_ARGS ?= "-F4"

# args to openssl req (Default is -batch for non interactive mode and
# -new for new certificate)
FIT_KEY_REQ_ARGS ?= "-batch -new"
UBOOT_FIT_KEY_REQ_ARGS ?= "-batch -new"

# Standard format for public key certificate
FIT_KEY_SIGN_PKCS ?= "-x509"
UBOOT_FIT_KEY_SIGN_PKCS ?= "-x509"

# Functions on this bbclass can apply to either U-boot or Kernel,
# depending on the scenario
UBOOT_PN = "${@d.getVar('PREFERRED_PROVIDER_u-boot') or 'u-boot'}"
KERNEL_PN = "${@d.getVar('PREFERRED_PROVIDER_virtual/kernel')}"

# We need u-boot-tools-native if we're creating a U-Boot fitImage
python() {
    if d.getVar('UBOOT_FITIMAGE_ENABLE') == '1':
        depends = d.getVar("DEPENDS")
        depends = "%s u-boot-tools-native dtc-native" % depends
        d.setVar("DEPENDS", depends)
}

concat_dtb_helper() {
	if [ -e "${UBOOT_DTB_BINARY}" ]; then
		ln -sf ${UBOOT_DTB_IMAGE} ${DEPLOYDIR}/${UBOOT_DTB_BINARY}
		ln -sf ${UBOOT_DTB_IMAGE} ${DEPLOYDIR}/${UBOOT_DTB_SYMLINK}
	fi

	if [ -f "${UBOOT_NODTB_BINARY}" ]; then
		install ${UBOOT_NODTB_BINARY} ${DEPLOYDIR}/${UBOOT_NODTB_IMAGE}
		ln -sf ${UBOOT_NODTB_IMAGE} ${DEPLOYDIR}/${UBOOT_NODTB_SYMLINK}
		ln -sf ${UBOOT_NODTB_IMAGE} ${DEPLOYDIR}/${UBOOT_NODTB_BINARY}
	fi

	# If we're not using a signed u-boot fit, concatenate SPL w/o DTB & U-Boot DTB
	# with public key (otherwise it will be deployed by the equivalent
	# concat_spl_dtb_helper function - cf. kernel-fitimage.bbclass for more details)
	if [ "${SPL_SIGN_ENABLE}" != "1" ] ; then
		deployed_uboot_dtb_binary='${DEPLOY_DIR_IMAGE}/${UBOOT_DTB_IMAGE}'
		if [ "x${UBOOT_SUFFIX}" = "ximg" -o "x${UBOOT_SUFFIX}" = "xrom" ] && \
			[ -e "$deployed_uboot_dtb_binary" ]; then
			oe_runmake EXT_DTB=$deployed_uboot_dtb_binary
			install ${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}
		elif [ -e "${DEPLOYDIR}/${UBOOT_NODTB_IMAGE}" -a -e "$deployed_uboot_dtb_binary" ]; then
			cd ${DEPLOYDIR}
			cat ${UBOOT_NODTB_IMAGE} $deployed_uboot_dtb_binary | tee ${B}/${CONFIG_B_PATH}/${UBOOT_BINARY} > ${UBOOT_IMAGE}

			if [ -n "${UBOOT_CONFIG}" ]
			then
				i=0
				j=0
				for config in ${UBOOT_MACHINE}; do
					i=$(expr $i + 1);
					for type in ${UBOOT_CONFIG}; do
						j=$(expr $j + 1);
						if [ $j -eq $i ]
						then
							cp ${UBOOT_IMAGE} ${B}/${CONFIG_B_PATH}/u-boot-$type.${UBOOT_SUFFIX}
						fi
					done
				done
			fi
		else
			bbwarn "Failure while adding public key to u-boot binary. Verified boot won't be available."
		fi
	fi
}

concat_spl_dtb_helper() {

	# We only deploy symlinks to the u-boot-spl.dtb,as the KERNEL_PN will
	# be responsible for deploying the real file
	if [ -e "${SPL_DIR}/${SPL_DTB_BINARY}" ] ; then
		ln -sf ${SPL_DTB_IMAGE} ${DEPLOYDIR}/${SPL_DTB_SYMLINK}
		ln -sf ${SPL_DTB_IMAGE} ${DEPLOYDIR}/${SPL_DTB_BINARY}
	fi

	# Concatenate the SPL nodtb binary and u-boot.dtb
	deployed_spl_dtb_binary='${DEPLOY_DIR_IMAGE}/${SPL_DTB_IMAGE}'
	if [ -e "${DEPLOYDIR}/${SPL_NODTB_IMAGE}" -a -e "$deployed_spl_dtb_binary" ] ; then
		cd ${DEPLOYDIR}
		cat ${SPL_NODTB_IMAGE} $deployed_spl_dtb_binary | tee ${B}/${CONFIG_B_PATH}/${SPL_BINARY} > ${SPL_IMAGE}
	else
		bbwarn "Failure while adding public key to spl binary. Verified U-Boot boot won't be available."
	fi
}


concat_dtb() {
	if [ "${UBOOT_SIGN_ENABLE}" = "1" -a "${PN}" = "${UBOOT_PN}" -a -n "${UBOOT_DTB_BINARY}" ]; then
		mkdir -p ${DEPLOYDIR}
		if [ -n "${UBOOT_CONFIG}" ]; then
			for config in ${UBOOT_MACHINE}; do
				CONFIG_B_PATH="$config"
				cd ${B}/$config
				concat_dtb_helper
			done
		else
			CONFIG_B_PATH=""
			cd ${B}
			concat_dtb_helper
		fi
	fi
}

concat_spl_dtb() {
	if [ "${SPL_SIGN_ENABLE}" = "1" -a "${PN}" = "${UBOOT_PN}" -a -n "${SPL_DTB_BINARY}" ]; then
		mkdir -p ${DEPLOYDIR}
		if [ -n "${UBOOT_CONFIG}" ]; then
			for config in ${UBOOT_MACHINE}; do
				CONFIG_B_PATH="$config"
				cd ${B}/$config
				concat_spl_dtb_helper
			done
		else
			CONFIG_B_PATH=""
			cd ${B}
			concat_spl_dtb_helper
		fi
	fi
}


# Install UBOOT_DTB_BINARY to datadir, so that kernel can use it for
# signing, and kernel will deploy UBOOT_DTB_BINARY after signs it.
install_helper() {
	if [ -f "${UBOOT_DTB_BINARY}" ]; then
		# UBOOT_DTB_BINARY is a symlink to UBOOT_DTB_IMAGE, so we
		# need both of them.
		install -Dm 0644 ${UBOOT_DTB_BINARY} ${D}${datadir}/${UBOOT_DTB_IMAGE}
		ln -sf ${UBOOT_DTB_IMAGE} ${D}${datadir}/${UBOOT_DTB_BINARY}
	else
		bbwarn "${UBOOT_DTB_BINARY} not found"
	fi
}

# Install SPL dtb and u-boot nodtb to datadir,
install_spl_helper() {
	if [ -f "${SPL_DIR}/${SPL_DTB_BINARY}" ]; then
		install -Dm 0644 ${SPL_DIR}/${SPL_DTB_BINARY} ${D}${datadir}/${SPL_DTB_IMAGE}
		ln -sf ${SPL_DTB_IMAGE} ${D}${datadir}/${SPL_DTB_BINARY}
	else
		bbwarn "${SPL_DTB_BINARY} not found"
	fi
	if [ -f "${UBOOT_NODTB_BINARY}" ] ; then
		install -Dm 0644 ${UBOOT_NODTB_BINARY} ${D}${datadir}/${UBOOT_NODTB_IMAGE}
		ln -sf ${UBOOT_NODTB_IMAGE} ${D}${datadir}/${UBOOT_NODTB_BINARY}
	else
		bbwarn "${UBOOT_NODTB_BINARY} not found"
	fi

	# We need to install a 'stub' u-boot-fitimage + its to datadir,
	# so that the KERNEL_PN can use the correct filename when
	# assembling and deploying them
	touch ${D}/${datadir}/${UBOOT_FITIMAGE_IMAGE}
	touch ${D}/${datadir}/${UBOOT_ITS_IMAGE}
}

do_install:append() {
	if [ "${PN}" = "${UBOOT_PN}" ]; then
		if [ -n "${UBOOT_CONFIG}" ]; then
			for config in ${UBOOT_MACHINE}; do
				cd ${B}/$config
				if [ "${UBOOT_SIGN_ENABLE}" = "1" -o "${UBOOT_FITIMAGE_ENABLE}" = "1" ] && \
					[ -n "${UBOOT_DTB_BINARY}" ]; then
					install_helper
				fi
				if [ "${UBOOT_FITIMAGE_ENABLE}" = "1" -a -n "${SPL_DTB_BINARY}" ]; then
					install_spl_helper
				fi
			done
		else
			cd ${B}
			if [ "${UBOOT_SIGN_ENABLE}" = "1" -o "${UBOOT_FITIMAGE_ENABLE}" = "1" ] && \
				[ -n "${UBOOT_DTB_BINARY}" ]; then
				install_helper
			fi
			if [ "${UBOOT_FITIMAGE_ENABLE}" = "1" -a -n "${SPL_DTB_BINARY}" ]; then
				install_spl_helper
			fi
		fi
	fi
}

do_uboot_generate_rsa_keys() {
	if [ "${SPL_SIGN_ENABLE}" = "0" ] && [ "${UBOOT_FIT_GENERATE_KEYS}" = "1" ]; then
		bbwarn "UBOOT_FIT_GENERATE_KEYS is set to 1 eventhough SPL_SIGN_ENABLE is set to 0. The keys will not be generated as they won't be used."
	fi

	if [ "${SPL_SIGN_ENABLE}" = "1" ] && [ "${UBOOT_FIT_GENERATE_KEYS}" = "1" ]; then

		# Generate keys only if they don't already exist
		if [ ! -f "${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}".key ] || \
			[ ! -f "${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}".crt ]; then

			# make directory if it does not already exist
			mkdir -p "${SPL_SIGN_KEYDIR}"

			echo "Generating RSA private key for signing U-Boot fitImage"
			openssl genrsa ${UBOOT_FIT_KEY_GENRSA_ARGS} -out \
				"${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}".key \
				"${UBOOT_FIT_SIGN_NUMBITS}"

			echo "Generating certificate for signing U-Boot fitImage"
			openssl req ${UBOOT_FIT_KEY_REQ_ARGS} "${UBOOT_FIT_KEY_SIGN_PKCS}" \
				-key "${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}".key \
				-out "${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}".crt
		fi
	fi

}

addtask uboot_generate_rsa_keys before do_uboot_assemble_fitimage after do_compile

# Create a ITS file for the U-boot FIT, for use when
# we want to sign it so that the SPL can verify it
uboot_fitimage_assemble() {
	uboot_its="$1"
	uboot_nodtb_bin="$2"
	uboot_dtb="$3"
	uboot_bin="$4"
	spl_dtb="$5"
	uboot_csum="${UBOOT_FIT_HASH_ALG}"
	uboot_sign_algo="${UBOOT_FIT_SIGN_ALG}"
	uboot_sign_keyname="${SPL_SIGN_KEYNAME}"

	rm -f $uboot_its $uboot_bin

	# First we create the ITS script
	cat << EOF >> $uboot_its
/dts-v1/;

/ {
    description = "${UBOOT_FIT_DESC}";
    #address-cells = <1>;

    images {
        uboot {
            description = "U-Boot image";
            data = /incbin/("$uboot_nodtb_bin");
            type = "standalone";
            os = "u-boot";
            arch = "${UBOOT_ARCH}";
            compression = "none";
            load = <${UBOOT_LOADADDRESS}>;
            entry = <${UBOOT_ENTRYPOINT}>;
EOF

	if [ "${SPL_SIGN_ENABLE}" = "1" ] ; then
		cat << EOF >> $uboot_its
            signature {
                algo = "$uboot_csum,$uboot_sign_algo";
                key-name-hint = "$uboot_sign_keyname";
            };
EOF
	fi

	cat << EOF >> $uboot_its
        };
        fdt {
            description = "U-Boot FDT";
            data = /incbin/("$uboot_dtb");
            type = "flat_dt";
            arch = "${UBOOT_ARCH}";
            compression = "none";
EOF

	if [ "${SPL_SIGN_ENABLE}" = "1" ] ; then
		cat << EOF >> $uboot_its
            signature {
                algo = "$uboot_csum,$uboot_sign_algo";
                key-name-hint = "$uboot_sign_keyname";
            };
EOF
	fi

	cat << EOF >> $uboot_its
        };
    };

    configurations {
        default = "conf";
        conf {
            description = "Boot with signed U-Boot FIT";
            loadables = "uboot";
            fdt = "fdt";
        };
    };
};
EOF

	#
	# Assemble the U-boot FIT image
	#
	${UBOOT_MKIMAGE} \
		${@'-D "${SPL_MKIMAGE_DTCOPTS}"' if len('${SPL_MKIMAGE_DTCOPTS}') else ''} \
		-f $uboot_its \
		$uboot_bin

	if [ "${SPL_SIGN_ENABLE}" = "1" ] ; then
		#
		# Sign the U-boot FIT image and add public key to SPL dtb
		#
		${UBOOT_MKIMAGE_SIGN} \
			${@'-D "${SPL_MKIMAGE_DTCOPTS}"' if len('${SPL_MKIMAGE_DTCOPTS}') else ''} \
			-F -k "${SPL_SIGN_KEYDIR}" \
			-K "$spl_dtb" \
			-r $uboot_bin \
			${SPL_MKIMAGE_SIGN_ARGS}
	fi

}

do_uboot_assemble_fitimage() {
	# This function runs in KERNEL_PN context. The reason for that is that we need to
	# support the scenario where UBOOT_SIGN_ENABLE is placing the Kernel fitImage's
	# pubkey in the u-boot.dtb file, so that we can use it when building the U-Boot
	# fitImage itself.
	if [ "${UBOOT_FITIMAGE_ENABLE}" = "1" ] && \
	   [ -n "${SPL_DTB_BINARY}" -a "${PN}" = "${KERNEL_PN}" ] ; then
		if [ "${UBOOT_SIGN_ENABLE}" != "1" ]; then
			# If we're not signing the Kernel fitImage, that means
			# we need to copy the u-boot.dtb from staging ourselves
			cp -P ${STAGING_DATADIR}/u-boot*.dtb ${B}
		fi
		# As we are in the kernel context, we need to copy u-boot-spl.dtb from staging first.
		# Unfortunately, need to glob on top of ${SPL_DTB_BINARY} since _IMAGE and _SYMLINK
		# will contain U-boot's PV
		# Similarly, we need to get the filename for the 'stub' u-boot-fitimage + its in
		# staging so that we can use it for creating the image with the correct filename
		# in the KERNEL_PN context.
		# As for the u-boot.dtb (with fitimage's pubkey), it should come from the dependent
		# do_assemble_fitimage task
		cp -P ${STAGING_DATADIR}/u-boot-spl*.dtb ${B}
		cp -P ${STAGING_DATADIR}/u-boot-nodtb*.bin ${B}
		rm -rf ${B}/u-boot-fitImage-* ${B}/u-boot-its-*
		kernel_uboot_fitimage_name=`basename ${STAGING_DATADIR}/u-boot-fitImage-*`
		kernel_uboot_its_name=`basename ${STAGING_DATADIR}/u-boot-its-*`
		cd ${B}
		uboot_fitimage_assemble $kernel_uboot_its_name ${UBOOT_NODTB_BINARY} \
					${UBOOT_DTB_BINARY} $kernel_uboot_fitimage_name \
					${SPL_DTB_BINARY}
	fi
}

addtask uboot_assemble_fitimage before do_deploy after do_compile

do_deploy:prepend:pn-${UBOOT_PN}() {
	if [ "${UBOOT_SIGN_ENABLE}" = "1" -a -n "${UBOOT_DTB_BINARY}" ] ; then
		concat_dtb
	fi

	if [ "${UBOOT_FITIMAGE_ENABLE}" = "1" ] ; then
	# Deploy the u-boot-nodtb binary and symlinks...
		if [ -f "${SPL_DIR}/${SPL_NODTB_BINARY}" ] ; then
			echo "Copying u-boot-nodtb binary..."
			install -m 0644 ${SPL_DIR}/${SPL_NODTB_BINARY} ${DEPLOYDIR}/${SPL_NODTB_IMAGE}
			ln -sf ${SPL_NODTB_IMAGE} ${DEPLOYDIR}/${SPL_NODTB_SYMLINK}
			ln -sf ${SPL_NODTB_IMAGE} ${DEPLOYDIR}/${SPL_NODTB_BINARY}
		fi


		# We only deploy the symlinks to the uboot-fitImage and uboot-its
		# images, as the KERNEL_PN will take care of deploying the real file
		ln -sf ${UBOOT_FITIMAGE_IMAGE} ${DEPLOYDIR}/${UBOOT_FITIMAGE_BINARY}
		ln -sf ${UBOOT_FITIMAGE_IMAGE} ${DEPLOYDIR}/${UBOOT_FITIMAGE_SYMLINK}
		ln -sf ${UBOOT_ITS_IMAGE} ${DEPLOYDIR}/${UBOOT_ITS}
		ln -sf ${UBOOT_ITS_IMAGE} ${DEPLOYDIR}/${UBOOT_ITS_SYMLINK}
	fi

	if [ "${SPL_SIGN_ENABLE}" = "1" -a -n "${SPL_DTB_BINARY}" ] ; then
		concat_spl_dtb
	fi


}

do_deploy:append:pn-${UBOOT_PN}() {
	# If we're creating a u-boot fitImage, point u-boot.bin
	# symlink since it might get used by image recipes
	if [ "${UBOOT_FITIMAGE_ENABLE}" = "1" ] ; then
		ln -sf ${UBOOT_FITIMAGE_IMAGE} ${DEPLOYDIR}/${UBOOT_BINARY}
		ln -sf ${UBOOT_FITIMAGE_IMAGE} ${DEPLOYDIR}/${UBOOT_SYMLINK}
	fi
}

python () {
    if (   (d.getVar('UBOOT_SIGN_ENABLE') == '1'
            or d.getVar('UBOOT_FITIMAGE_ENABLE') == '1')
        and d.getVar('PN') == d.getVar('UBOOT_PN')
        and d.getVar('UBOOT_DTB_BINARY')):

        # Make "bitbake u-boot -cdeploy" deploys the signed u-boot.dtb
        # and/or the U-Boot fitImage
        d.appendVarFlag('do_deploy', 'depends', ' %s:do_deploy' % d.getVar('KERNEL_PN'))

    if d.getVar('UBOOT_FITIMAGE_ENABLE') == '1' and d.getVar('PN') == d.getVar('KERNEL_PN'):
        # As the U-Boot fitImage is created by the KERNEL_PN, we need
        # to make sure that the u-boot-spl.dtb and u-boot-spl-nodtb.bin
        # files are in the staging dir for it's use
        d.appendVarFlag('do_uboot_assemble_fitimage', 'depends', ' %s:do_populate_sysroot' % d.getVar('UBOOT_PN'))

        # If the Kernel fitImage is being signed, we need to
        # create the U-Boot fitImage after it
        if d.getVar('UBOOT_SIGN_ENABLE') == '1':
            d.appendVarFlag('do_uboot_assemble_fitimage', 'depends', ' %s:do_assemble_fitimage' % d.getVar('KERNEL_PN'))
            d.appendVarFlag('do_uboot_assemble_fitimage', 'depends', ' %s:do_assemble_fitimage_initramfs' % d.getVar('KERNEL_PN'))

}
