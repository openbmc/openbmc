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
    case '${type}' in \
        ext*) \
            bbnote 'Running e2fsprogs-derived ext2simg_android..' ; \
            ext2simg_android '${IMAGE_NAME}.${type}' '${IMAGE_NAME}.${type}.simg' || bberror 'ext2simg_android failed' ;\
            ln -sf '${IMAGE_NAME}.${type}.simg' '${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${type}.simg'; \
            ;; \
        *) \
            bbnote 'Generating sparse image for non-ext filesystem...'; \
            img2simg -s '${IMAGE_NAME}.${type}' '${IMAGE_NAME}.${type}.sparse' ${SPARSE_BLOCK_SIZE}; \
            ln -sf '${IMAGE_NAME}.${type}.sparse' '${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${type}.sparse'; \
            ;; \
    esac; \
    if [ "${DELETE_RAWIMAGE_AFTER_SPARSE_CMD}" = "1" ]; then \
        rm -f ${IMAGE_NAME}.${type};\
        bbnote "Raw file ${IMAGE_NAME}.${type} removed" ;\
    fi;\
 "

CONVERSION_DEPENDS_sparse = "android-tools-native e2fsprogs-ext4sparse-native"
do_image_ext4[depends] += "e2fsprogs-ext4sparse-native:do_populate_sysroot"
