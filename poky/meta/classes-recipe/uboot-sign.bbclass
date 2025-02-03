#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# This file is part of U-Boot verified boot support and is intended to be
# inherited from the u-boot recipe.
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
# For more details on signature process, please refer to U-Boot documentation.

# We need some variables from u-boot-config
inherit uboot-config
require conf/image-fitimage.conf

# Enable use of a U-Boot fitImage
UBOOT_FITIMAGE_ENABLE ?= "0"

# Signature activation - this requires UBOOT_FITIMAGE_ENABLE = "1"
SPL_SIGN_ENABLE ?= "0"

# Default value for deployment filenames.
UBOOT_DTB_IMAGE ?= "u-boot-${MACHINE}-${PV}-${PR}.dtb"
UBOOT_DTB_BINARY ?= "u-boot.dtb"
UBOOT_DTB_SIGNED ?= "${UBOOT_DTB_BINARY}-signed"
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
SPL_DTB_SIGNED ?= "${SPL_DTB_BINARY}-signed"
SPL_DTB_SYMLINK ?= "u-boot-spl-${MACHINE}.dtb"
SPL_NODTB_IMAGE ?= "u-boot-spl-nodtb-${MACHINE}-${PV}-${PR}.bin"
SPL_NODTB_BINARY ?= "u-boot-spl-nodtb.bin"
SPL_NODTB_SYMLINK ?= "u-boot-spl-nodtb-${MACHINE}.bin"

# U-Boot fitImage description
UBOOT_FIT_DESC ?= "U-Boot fitImage for ${DISTRO_NAME}/${PV}/${MACHINE}"

# U-Boot fitImage Hash Algo
UBOOT_FIT_HASH_ALG ?= "sha256"

# U-Boot fitImage Signature Algo
UBOOT_FIT_SIGN_ALG ?= "rsa2048"

# Generate keys for signing U-Boot fitImage
UBOOT_FIT_GENERATE_KEYS ?= "0"

# Size of private keys in number of bits
UBOOT_FIT_SIGN_NUMBITS ?= "2048"

# args to openssl genrsa (Default is just the public exponent)
UBOOT_FIT_KEY_GENRSA_ARGS ?= "-F4"

# args to openssl req (Default is -batch for non interactive mode and
# -new for new certificate)
UBOOT_FIT_KEY_REQ_ARGS ?= "-batch -new"

# Standard format for public key certificate
UBOOT_FIT_KEY_SIGN_PKCS ?= "-x509"

# length of address in number of <u32> cells
# ex: 1 32bits address, 2 64bits address
UBOOT_FIT_ADDRESS_CELLS ?= "1"

UBOOT_FIT_UBOOT_LOADADDRESS ?= "${UBOOT_LOADADDRESS}"
UBOOT_FIT_UBOOT_ENTRYPOINT ?= "${UBOOT_ENTRYPOINT}"

python() {
    # We need u-boot-tools-native if we're creating a U-Boot fitImage
    sign = d.getVar('UBOOT_SIGN_ENABLE') == '1'
    if d.getVar('UBOOT_FITIMAGE_ENABLE') == '1' or sign:
        d.appendVar('DEPENDS', " u-boot-tools-native dtc-native")
}

concat_dtb() {
	type="$1"
	binary="$2"

	if [ -e "${UBOOT_DTB_BINARY}" ]; then
		# Re-sign the kernel in order to add the keys to our dtb
		UBOOT_MKIMAGE_MODE="auto-conf"
		# Signing individual images is not recommended as that
		# makes fitImage susceptible to mix-and-match attack.
		if [ "${FIT_SIGN_INDIVIDUAL}" = "1" ] ; then
			UBOOT_MKIMAGE_MODE="auto"
		fi
		${UBOOT_MKIMAGE_SIGN} \
			${@'-D "${UBOOT_MKIMAGE_DTCOPTS}"' if len('${UBOOT_MKIMAGE_DTCOPTS}') else ''} \
			-f $UBOOT_MKIMAGE_MODE \
			-k "${UBOOT_SIGN_KEYDIR}" \
			-o "${FIT_HASH_ALG},${FIT_SIGN_ALG}" \
			-g "${UBOOT_SIGN_IMG_KEYNAME}" \
			-K "${UBOOT_DTB_BINARY}" \
			-d /dev/null \
			-r ${B}/unused.itb \
			${UBOOT_MKIMAGE_SIGN_ARGS}
		# Verify the kernel image and u-boot dtb
		${UBOOT_FIT_CHECK_SIGN} \
			-k "${UBOOT_DTB_BINARY}" \
			-f ${B}/unused.itb
		cp ${UBOOT_DTB_BINARY} ${UBOOT_DTB_SIGNED}
	fi

	# If we're not using a signed u-boot fit, concatenate SPL w/o DTB & U-Boot DTB
	# with public key (otherwise U-Boot will be packaged by uboot_fitimage_assemble)
	if [ "${SPL_SIGN_ENABLE}" != "1" ] ; then
		if [ "x${UBOOT_SUFFIX}" = "ximg" -o "x${UBOOT_SUFFIX}" = "xrom" ] && \
			[ -e "${UBOOT_DTB_BINARY}" ]; then
			oe_runmake EXT_DTB="${UBOOT_DTB_SIGNED}" ${UBOOT_MAKE_TARGET}
			if [ -n "${binary}" ]; then
				cp ${binary} ${UBOOT_BINARYNAME}-${type}.${UBOOT_SUFFIX}
			fi
		elif [ -e "${UBOOT_NODTB_BINARY}" -a -e "${UBOOT_DTB_BINARY}" ]; then
			if [ -n "${binary}" ]; then
				cat ${UBOOT_NODTB_BINARY} ${UBOOT_DTB_SIGNED} | tee ${binary} > \
					${UBOOT_BINARYNAME}-${type}.${UBOOT_SUFFIX}
			else
				cat ${UBOOT_NODTB_BINARY} ${UBOOT_DTB_SIGNED} > ${UBOOT_BINARY}
			fi
		else
			bbwarn "Failure while adding public key to u-boot binary. Verified boot won't be available."
		fi
	fi
}

deploy_dtb() {
	type="$1"

	if [ -n "${type}" ]; then
		uboot_dtb_binary="u-boot-${type}-${PV}-${PR}.dtb"
		uboot_nodtb_binary="u-boot-nodtb-${type}-${PV}-${PR}.bin"
	else
		uboot_dtb_binary="${UBOOT_DTB_IMAGE}"
		uboot_nodtb_binary="${UBOOT_NODTB_IMAGE}"
	fi

	if [ -e "${UBOOT_DTB_SIGNED}" ]; then
		install -Dm644 ${UBOOT_DTB_SIGNED} ${DEPLOYDIR}/${uboot_dtb_binary}
		if [ -n "${type}" ]; then
			ln -sf ${uboot_dtb_binary} ${DEPLOYDIR}/${UBOOT_DTB_IMAGE}
		fi
	fi

	if [ -f "${UBOOT_NODTB_BINARY}" ]; then
		install -Dm644 ${UBOOT_NODTB_BINARY} ${DEPLOYDIR}/${uboot_nodtb_binary}
		if [ -n "${type}" ]; then
			ln -sf ${uboot_nodtb_binary} ${DEPLOYDIR}/${UBOOT_NODTB_IMAGE}
		fi
	fi
}

concat_spl_dtb() {
	if [ -e "${SPL_DIR}/${SPL_NODTB_BINARY}" -a -e "${SPL_DIR}/${SPL_DTB_BINARY}" ] ; then
		cat ${SPL_DIR}/${SPL_NODTB_BINARY} ${SPL_DIR}/${SPL_DTB_SIGNED} > "${SPL_BINARY}"
	else
		bbwarn "Failure while adding public key to spl binary. Verified U-Boot boot won't be available."
	fi
}

deploy_spl_dtb() {
	type="$1"

	if [ -n "${type}" ]; then
		spl_dtb_binary="u-boot-spl-${type}-${PV}-${PR}.dtb"
		spl_nodtb_binary="u-boot-spl-nodtb-${type}-${PV}-${PR}.bin"
	else
		spl_dtb_binary="${SPL_DTB_IMAGE}"
		spl_nodtb_binary="${SPL_NODTB_IMAGE}"
	fi

	if [ -e "${SPL_DIR}/${SPL_DTB_SIGNED}" ] ; then
		install -Dm644 ${SPL_DIR}/${SPL_DTB_SIGNED} ${DEPLOYDIR}/${spl_dtb_binary}
		if [ -n "${type}" ]; then
			ln -sf ${spl_dtb_binary} ${DEPLOYDIR}/${SPL_DTB_IMAGE}
		fi
	fi

	if [ -f "${SPL_DIR}/${SPL_NODTB_BINARY}" ] ; then
		install -Dm644 ${SPL_DIR}/${SPL_NODTB_BINARY} ${DEPLOYDIR}/${spl_nodtb_binary}
		if [ -n "${type}" ]; then
			ln -sf ${spl_nodtb_binary} ${DEPLOYDIR}/${SPL_NODTB_IMAGE}
		fi
	fi

	# For backwards compatibility...
	install -Dm644 ${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}
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
	rm -f ${UBOOT_ITS} ${UBOOT_FITIMAGE_BINARY}

	# First we create the ITS script
	cat << EOF >> ${UBOOT_ITS}
/dts-v1/;

/ {
    description = "${UBOOT_FIT_DESC}";
    #address-cells = <${UBOOT_FIT_ADDRESS_CELLS}>;

    images {
        uboot {
            description = "U-Boot image";
            data = /incbin/("${UBOOT_NODTB_BINARY}");
            type = "standalone";
            os = "u-boot";
            arch = "${UBOOT_ARCH}";
            compression = "none";
            load = <${UBOOT_FIT_UBOOT_LOADADDRESS}>;
            entry = <${UBOOT_FIT_UBOOT_ENTRYPOINT}>;
EOF

	if [ "${SPL_SIGN_ENABLE}" = "1" ] ; then
		cat << EOF >> ${UBOOT_ITS}
            signature {
                algo = "${UBOOT_FIT_HASH_ALG},${UBOOT_FIT_SIGN_ALG}";
                key-name-hint = "${SPL_SIGN_KEYNAME}";
            };
EOF
	fi

	cat << EOF >> ${UBOOT_ITS}
        };
        fdt {
            description = "U-Boot FDT";
            data = /incbin/("${UBOOT_DTB_BINARY}");
            type = "flat_dt";
            arch = "${UBOOT_ARCH}";
            compression = "none";
EOF

	if [ "${SPL_SIGN_ENABLE}" = "1" ] ; then
		cat << EOF >> ${UBOOT_ITS}
            signature {
                algo = "${UBOOT_FIT_HASH_ALG},${UBOOT_FIT_SIGN_ALG}";
                key-name-hint = "${SPL_SIGN_KEYNAME}";
            };
EOF
	fi

	cat << EOF >> ${UBOOT_ITS}
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
		-f ${UBOOT_ITS} \
		${UBOOT_FITIMAGE_BINARY}

	if [ "${SPL_SIGN_ENABLE}" = "1" ] ; then
		#
		# Sign the U-boot FIT image and add public key to SPL dtb
		#
		${UBOOT_MKIMAGE_SIGN} \
			${@'-D "${SPL_MKIMAGE_DTCOPTS}"' if len('${SPL_MKIMAGE_DTCOPTS}') else ''} \
			-F -k "${SPL_SIGN_KEYDIR}" \
			-K "${SPL_DIR}/${SPL_DTB_BINARY}" \
			-r ${UBOOT_FITIMAGE_BINARY} \
			${SPL_MKIMAGE_SIGN_ARGS}
		#
		# Verify the U-boot FIT image and SPL dtb
		#
		${UBOOT_FIT_CHECK_SIGN} \
			-k "${SPL_DIR}/${SPL_DTB_BINARY}" \
			-f ${UBOOT_FITIMAGE_BINARY}
	fi

	if [ -e "${SPL_DIR}/${SPL_DTB_BINARY}" ]; then
		cp ${SPL_DIR}/${SPL_DTB_BINARY} ${SPL_DIR}/${SPL_DTB_SIGNED}
	fi
}

uboot_assemble_fitimage_helper() {
	type="$1"
	binary="$2"

	if [ "${UBOOT_SIGN_ENABLE}" = "1" -a -n "${UBOOT_DTB_BINARY}" ] ; then
		concat_dtb "$type" "$binary"
	fi

	if [ "${UBOOT_FITIMAGE_ENABLE}" = "1" -a -n "${SPL_DTB_BINARY}" ]; then
		uboot_fitimage_assemble
	fi

	if [ "${SPL_SIGN_ENABLE}" = "1" -a -n "${SPL_DTB_BINARY}" ] ; then
		concat_spl_dtb
	fi
}

do_uboot_assemble_fitimage() {
	if [ -n "${UBOOT_CONFIG}" ]; then
		unset i
		for config in ${UBOOT_MACHINE}; do
			unset j k
			i=$(expr $i + 1);
			for type in ${UBOOT_CONFIG}; do
				j=$(expr $j + 1);
				if [ $j -eq $i ]; then
					break;
				fi
			done

			for binary in ${UBOOT_BINARIES}; do
				k=$(expr $k + 1);
				if [ $k -eq $i ]; then
					break;
				fi
			done

			cd ${B}/${config}
			uboot_assemble_fitimage_helper ${type} ${binary}
		done
	else
		cd ${B}
		uboot_assemble_fitimage_helper "" ${UBOOT_BINARY}
	fi
}

addtask uboot_assemble_fitimage before do_install do_deploy after do_compile

deploy_helper() {
	type="$1"

	if [ "${UBOOT_SIGN_ENABLE}" = "1" -a -n "${UBOOT_DTB_SIGNED}" ] ; then
		deploy_dtb $type
	fi

	if [ "${UBOOT_FITIMAGE_ENABLE}" = "1" -a -n "${SPL_DTB_BINARY}" ]; then
		if [ -n "${type}" ]; then
			uboot_its_image="u-boot-its-${type}-${PV}-${PR}"
			uboot_fitimage_image="u-boot-fitImage-${type}-${PV}-${PR}"
		else
			uboot_its_image="${UBOOT_ITS_IMAGE}"
			uboot_fitimage_image="${UBOOT_FITIMAGE_IMAGE}"
		fi

		install -Dm644 ${UBOOT_FITIMAGE_BINARY} ${DEPLOYDIR}/$uboot_fitimage_image
		install -Dm644 ${UBOOT_ITS} ${DEPLOYDIR}/$uboot_its_image

		if [ -n "${type}" ]; then
			ln -sf $uboot_its_image ${DEPLOYDIR}/${UBOOT_ITS_IMAGE}
			ln -sf $uboot_fitimage_image ${DEPLOYDIR}/${UBOOT_FITIMAGE_IMAGE}
		fi
	fi

	if [ "${SPL_SIGN_ENABLE}" = "1" -a -n "${SPL_DTB_SIGNED}" ] ; then
		deploy_spl_dtb $type
	fi
}

do_deploy:prepend() {
	if [ -n "${UBOOT_CONFIG}" ]; then
		unset i j k
		for config in ${UBOOT_MACHINE}; do
			i=$(expr $i + 1);
			for type in ${UBOOT_CONFIG}; do
				j=$(expr $j + 1);
				if [ $j -eq $i ]; then
					cd ${B}/${config}
					deploy_helper ${type}
				fi
			done
			unset j
		done
		unset i
	else
		cd ${B}
		deploy_helper ""
	fi

	if [ "${UBOOT_SIGN_ENABLE}" = "1" -a -n "${UBOOT_DTB_BINARY}" ] ; then
		ln -sf ${UBOOT_DTB_IMAGE} ${DEPLOYDIR}/${UBOOT_DTB_BINARY}
		ln -sf ${UBOOT_DTB_IMAGE} ${DEPLOYDIR}/${UBOOT_DTB_SYMLINK}
		ln -sf ${UBOOT_NODTB_IMAGE} ${DEPLOYDIR}/${UBOOT_NODTB_SYMLINK}
		ln -sf ${UBOOT_NODTB_IMAGE} ${DEPLOYDIR}/${UBOOT_NODTB_BINARY}
	fi

	if [ "${UBOOT_FITIMAGE_ENABLE}" = "1" ] ; then
		ln -sf ${UBOOT_ITS_IMAGE} ${DEPLOYDIR}/${UBOOT_ITS}
		ln -sf ${UBOOT_ITS_IMAGE} ${DEPLOYDIR}/${UBOOT_ITS_SYMLINK}
		ln -sf ${UBOOT_FITIMAGE_IMAGE} ${DEPLOYDIR}/${UBOOT_FITIMAGE_BINARY}
		ln -sf ${UBOOT_FITIMAGE_IMAGE} ${DEPLOYDIR}/${UBOOT_FITIMAGE_SYMLINK}
	fi

	if [ "${SPL_SIGN_ENABLE}" = "1" -a -n "${SPL_DTB_BINARY}" ] ; then
		ln -sf ${SPL_DTB_IMAGE} ${DEPLOYDIR}/${SPL_DTB_SYMLINK}
		ln -sf ${SPL_DTB_IMAGE} ${DEPLOYDIR}/${SPL_DTB_BINARY}
		ln -sf ${SPL_NODTB_IMAGE} ${DEPLOYDIR}/${SPL_NODTB_SYMLINK}
		ln -sf ${SPL_NODTB_IMAGE} ${DEPLOYDIR}/${SPL_NODTB_BINARY}
	fi
}

do_deploy:append() {
	# If we're creating a u-boot fitImage, point u-boot.bin
	# symlink since it might get used by image recipes
	if [ "${UBOOT_FITIMAGE_ENABLE}" = "1" ] ; then
		ln -sf ${UBOOT_FITIMAGE_IMAGE} ${DEPLOYDIR}/${UBOOT_BINARY}
		ln -sf ${UBOOT_FITIMAGE_IMAGE} ${DEPLOYDIR}/${UBOOT_SYMLINK}
	fi
}
