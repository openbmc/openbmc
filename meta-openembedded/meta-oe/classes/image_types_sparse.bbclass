inherit image_types

CONVERSIONTYPES += "sparse"
CONVERSION_CMD:sparse = " \
    case "${type}" in \
        ext*) \
            ext2simg "${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}" \
                     "${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sparse" \
            ;; \
        *) \
            img2simg "${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}" \
                     "${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sparse" \
            ;; \
    esac \
"
CONVERSION_DEPENDS_sparse = "android-tools-native"
