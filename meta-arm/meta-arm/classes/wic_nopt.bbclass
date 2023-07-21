# This class removes the empty partition table header
# in the WIC file when --no-table WKS option is used

IMAGE_TYPES:append = " wic.nopt"

CONVERSIONTYPES += "nopt"

# 1024 bytes are skipped which corresponds to the size of the partition table header to remove
CONVERSION_CMD:nopt = "tail -c +1025 ${IMAGE_NAME}.${type} >  ${IMAGE_NAME}.${type}.nopt"
