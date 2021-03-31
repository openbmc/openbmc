inherit image_types

CONVERSIONTYPES += "sparse"
CONVERSION_CMD_sparse() {
    in="${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"
    out="${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sparse"
    case "${type}" in
        ext*)
            ext2simg "$in" "$out"
            ;;
        *)
            img2simg "$in" "$out"
            ;;
    esac
}
CONVERSION_DEPENDS_sparse = "android-tools-native"
