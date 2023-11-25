SUMMARY = "Arm SystemReady Firmware Image"
DESCRIPTION = "This recipe ensures that all packages listed in \
ARM_SYSTEMREADY_FIRMWARE variable (set at machine conf) are deployed."

EXTRA_IMAGEDEPENDS = "${ARM_SYSTEMREADY_FIRMWARE}"

inherit extra_imagedepends_only

python() {
    if not d.getVar("ARM_SYSTEMREADY_FIRMWARE"):
        raise bb.parse.SkipRecipe("ARM_SYSTEMREADY_FIRMWARE needs to be set")
}

do_testimage[noexec] = "1"
