# This class removes the empty partition table header
# in the WIC file when --no-table WKS option is used

IMAGE_TYPES += "wic.nopt"

CONVERSIONTYPES += "nopt"

# 1024 bytes are skipped which corresponds to the size of the partition table header to remove
CONVERSION_CMD:nopt = "tail -c +1025 ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} >  ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.nopt"
