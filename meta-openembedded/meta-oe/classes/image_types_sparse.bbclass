inherit image_types

CONVERSIONTYPES += "sparse"
CONVERSION_CMD:sparse = " \
    img2simg "${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}" \
             "${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sparse" \
"
CONVERSION_DEPENDS_sparse = "android-tools-native"
