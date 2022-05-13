#!/bin/sh
set -eo pipefail

system_key="/etc/activationdata/OpenBMC/publickey"
bmclog="/tmp/update-bmc.log"
image_path="/tmp/images/bmc-image"
image_file="/tmp/bmc.tar"


# verify file with signature and public key
# we default assume signature file as <file>.sig
# Usage: Verify <file>  <pub key>
Verify(){
    if [ ! -f "${1}" ];then
        echo "no such file exist: ${1}" > ${bmclog}
        exit 1
    fi
    r="$(openssl dgst -verify ${2} -sha256 -signature ${1}.sig ${1})"
    echo "file $1 verify result: $r" >> ${bmclog}
    if [[ "Verified OK" != "${r}" ]];then
        exit 1
    fi
}

# === main process ===
echo "Start bmc verify process" > ${bmclog}
# == untar image ==
echo "untar image from ${image_file}" >> ${bmclog}
if [ ! -d "${image_path}" ]; then
    mkdir -p "${image_path}"
fi
tar -xf "${image_file}" -C "${image_path}" >> ${bmclog}

# == verify system level key ==
echo "verify manifest and public key with system key" >> ${bmclog}
if [ ! -f "${system_key}" ]; then
    echo "no system public key exist: ${system_key}" > ${bmclog}
    exit 1
fi
Verify "${image_path}/publickey" ${system_key}
Verify "${image_path}/MANIFEST" ${system_key}

# == verify all image ==
echo "verify images with public key" >> ${bmclog}
pub_key="${image_path}/publickey"
Verify "${image_path}/image-kernel" ${pub_key}
Verify "${image_path}/image-u-boot" ${pub_key}
Verify "${image_path}/image-rofs" ${pub_key}
Verify "${image_path}/image-rwfs" ${pub_key}

# == verify full image sig ===
echo "verify full image signature" >> ${bmclog}
tmp_full="${image_path}/image-full"
# note. the image sequence should not change (sort by name)
cat "${image_path}/image-kernel.sig" "${image_path}/image-rofs.sig"  \
    "${image_path}/image-rwfs.sig" "${image_path}/image-u-boot.sig" \
    "${image_path}/MANIFEST.sig" "${image_path}/publickey.sig" > ${tmp_full}
Verify ${tmp_full} ${pub_key}

echo "verify image process finished" >> ${bmclog}
