ROOTFS_BOOTSTRAP_INSTALL = ""
IMAGE_TYPES_MASKED += "container"
IMAGE_TYPEDEP:container = "tar.bz2"

python __anonymous() {
    if "container" in d.getVar("IMAGE_FSTYPES") and \
       d.getVar("IMAGE_CONTAINER_NO_DUMMY") != "1" and \
       "linux-dummy" not in d.getVar("PREFERRED_PROVIDER_virtual/kernel"):
        msg = '"container" is in IMAGE_FSTYPES, but ' \
              'PREFERRED_PROVIDER_virtual/kernel is not "linux-dummy". ' \
              'Unless a particular kernel is needed, using linux-dummy will ' \
              'prevent a kernel from being built, which can reduce ' \
              'build times. If you don\'t want to use "linux-dummy", set ' \
              '"IMAGE_CONTAINER_NO_DUMMY" to "1".'

        # Raising skip recipe was Paul's clever idea. It causes the error to
        # only be shown for the recipes actually requested to build, rather
        # than bb.fatal which would appear for all recipes inheriting the
        # class.
        raise bb.parse.SkipRecipe(msg)
}
