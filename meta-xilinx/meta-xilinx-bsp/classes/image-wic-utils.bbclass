# Helper/utility functions to work with the IMAGE_BOOT_FILES variable and its
# expected behvaior with regards to the contents of the DEPLOY_DIR_IMAGE.
#
# The use of these functions assume that the deploy directory is populated with
# any dependent files/etc. Such that the recipe using these functions depends
# on the recipe that provides the files being used/queried.

def boot_files_split_expand(d):
    # IMAGE_BOOT_FILES has extra renaming info in the format '<source>;<target>'
    for f in (d.getVar("IMAGE_BOOT_FILES") or "").split(" "):
        parts = f.split(";", 1)
        sources = [parts[0].strip()]
        if "*" in parts[0]:
            # has glob part
            import glob
            deployroot = d.getVar("DEPLOY_DIR_IMAGE")
            sources = []
            for i in glob.glob(os.path.join(deployroot, parts[0])):
                sources.append(os.path.basename(i))

        # for all sources, yield an entry
        for s in sources:
            if len(parts) == 2:
                yield s, parts[1].strip()
            yield s, s

def boot_files_bitstream(d):
    expectedfiles = [("bitstream", True)]
    expectedexts = [(".bit", True), (".bin", False)]
    # search for bitstream paths, use the renamed file. First matching is used
    for source, target in boot_files_split_expand(d):
        # skip boot.bin and u-boot.bin, it is not a bitstream
        skip = ["boot.bin", "u-boot.bin"]
        if source in skip or target in skip:
            continue

        for e, t in expectedfiles:
            if source == e or target == e:
                return target, t
        for e, t in expectedexts:
            if source.endswith(e) or target.endswith(e):
                return target, t
    return "", False

def boot_files_dtb_filepath(d):
    dtbs = (d.getVar("IMAGE_BOOT_FILES") or "").split(" ")
    for source, target in boot_files_split_expand(d):
        if target.endswith(".dtb"):
            return target
    return ""

