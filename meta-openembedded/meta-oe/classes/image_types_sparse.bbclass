inherit image_types

# This sets the granularity of the sparse image conversion. Chunk sizes will be
# specified in units of this value. Setting this value smaller than the
# underlying image's block size will not result in any further space saving.
# However, there is no loss in correctness if this value is larger or smaller
# than optimal. This value should be a power of two.
SPARSE_BLOCK_SIZE ??= "4096"

CONVERSIONTYPES += "sparse"

DELETE_RAWIMAGE_AFTER_SPARSE_CMD ??= "0"

CONVERSION_CMD:sparse = " \
    truncate --no-create --size=%${SPARSE_BLOCK_SIZE} "${IMAGE_NAME}.${type}"; \
    img2simg -s "${IMAGE_NAME}.${type}" "${IMAGE_NAME}.${type}.sparse" ${SPARSE_BLOCK_SIZE}; \
    if [ "${DELETE_RAWIMAGE_AFTER_SPARSE_CMD}" = "1" ]; then \
        rm -f ${IMAGE_NAME}.${type};\
        bbwarn "Raw file ${IMAGE_NAME}.${type} removed" ;\
    fi;\
 "

CONVERSION_DEPENDS_sparse = "android-tools-native"
