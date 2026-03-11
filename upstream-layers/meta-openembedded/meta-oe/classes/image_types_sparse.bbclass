inherit image_types

# This sets the granularity of the sparse image conversion. Chunk sizes will be
# specified in units of this value. Setting this value smaller than the
# underlying image's block size will not result in any further space saving.
# However, there is no loss in correctness if this value is larger or smaller
# than optimal. This value should be a power of two.
SPARSE_BLOCK_SIZE ??= "4096"

CONVERSIONTYPES += "sparse"

CONVERSION_CMD:sparse = " \
    truncate --no-create --size=%${SPARSE_BLOCK_SIZE} "${IMAGE_NAME}.${type}"; \
    img2simg -s "${IMAGE_NAME}.${type}" "${IMAGE_NAME}.${type}.sparse" ${SPARSE_BLOCK_SIZE}; \
 "

CONVERSION_DEPENDS_sparse = "android-tools-native"
