require fvp-envelope.inc

SUMMARY = "Arm Fixed Virtual Platform - Armv-A Base RevC Architecture Envelope Model FVP"
LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=0c32ac6f58ebff83065105042ab98211 \
                    file://license_terms/third_party_licenses/arm_license_management_utilities/third_party_licenses.txt;md5=c09526c02e631abb95ad61528892552d"

SRC_URI[fvp-aarch64.sha256sum] = "d0925791ec4cfb99c3b4122afbb442ad076f92c4e173ed3bd092a3bf180d56f0"
SRC_URI[fvp-x86_64.sha256sum] = "9d43c8eb347bf169a024cdbbc04572d169f60098fe061316b36bdbd4a44ffd79"

MODEL_CODE = "FVP_Base_RevC-2xAEMvA"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
