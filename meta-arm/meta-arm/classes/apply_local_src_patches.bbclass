# This class is to be inherited by recipes where there are patches located inside
# the fetched source code which need to be applied.

# The following variables need to be set:
# LOCAL_SRC_PATCHES_INPUT_DIR is the directory from where the patches are located
# LOCAL_SRC_PATCHES_DEST_DIR is the directory where the patches will be applied

do_patch[depends] += "quilt-native:do_populate_sysroot"

LOCAL_SRC_PATCHES_INPUT_DIR ??= ""
LOCAL_SRC_PATCHES_DEST_DIR ??= "${LOCAL_SRC_PATCHES_INPUT_DIR}"

python() {
    if not d.getVar('LOCAL_SRC_PATCHES_INPUT_DIR'):
        bb.warn("LOCAL_SRC_PATCHES_INPUT_DIR variable needs to be set.")
}

apply_local_src_patches() {

    input_dir="${LOCAL_SRC_PATCHES_INPUT_DIR}"
    dest_dir="${LOCAL_SRC_PATCHES_DEST_DIR}"

    if [ ! -d "$input_dir" ] ; then
        bbfatal "LOCAL_SRC_PATCHES_INPUT_DIR=$input_dir not found."
    fi

    if [ ! -d "$dest_dir" ] ; then
        bbfatal "LOCAL_SRC_PATCHES_DEST_DIR=$dest_dir not found."
    fi

    cd $dest_dir
    export QUILT_PATCHES=./patches-extra
    mkdir -p patches-extra

    for patch in $(find $input_dir -type f -name *.patch -or -name *.diff | sort)
    do
        patch_basename=`basename $patch`
        if ! quilt applied $patch_basename >/dev/null ; then
            bbdebug 1 "Applying $patch_basename in $dest_dir."
            echo $patch_basename >> patches-extra/series
            cp $patch patches-extra
            quilt push $patch_basename
        else
            bbdebug 1 "$patch_basename already applied."
        fi
    done
}
do_patch[postfuncs] += "apply_local_src_patches"
