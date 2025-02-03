# SPDX-License-Identifier: MIT
#
# Copyright PHYTEC Messtechnik GmbH
# Copyright (C) 2024 Pengutronix, <yocto@pengutronix.de>
#
# Class for creating (signed) FIT images
# Description:
#
# You have to define the 'images' to put in the FIT image in your recipe file
# following this example:
#
#    FITIMAGE_IMAGES ?= "kernel fdt fdto setup ramdisk bootscript"
#
#    FITIMAGE_IMAGE_kernel ?= "virtual/kernel"
#    FITIMAGE_IMAGE_kernel[type] ?= "kernel"
#
#    FITIMAGE_IMAGE_fdt ?= "virtual/dtb" # or "virtual/kernel"
#    FITIMAGE_IMAGE_fdt[type] ?= "fdt"
#    #FITIMAGE_IMAGE_fdt[file] ?= "hw-name.dtb"
#
#    FITIMAGE_IMAGE_fdto ?= "virtual/kernel"
#    FITIMAGE_IMAGE_fdto[type] ?= "fdto"
#    FITIMAGE_IMAGE_fdto[file] ?= <list of all dtbo files from KERNEL_DEVICETREE>
#
#    Add a devicetree created on-thy-fly of a base dtb and serveral dtbo's
#    FITIMAGE_IMAGE_fdtapply ?= "virtual/kernel"
#    FITIMAGE_IMAGE_fdtapply[type] ?= "fdtapply"
#    FITIMAGE_IMAGE_fdtapply[file] ?= "base.dtb overlay-1.dtbo overlay-2.dtbo"
#    FITIMAGE_IMAGE_fdtapply[name] ?= "<name for new generated fdt>"
#
#    FITIMAGE_IMAGE_ramdisk ?= "core-image-minimal"
#    FITIMAGE_IMAGE_ramdisk[type] ?= "ramdisk"
#    FITIMAGE_IMAGE_ramdisk[fstype] ?= "cpio.gz"
#
#    FITIMAGE_IMAGE_bootscript ?= "bootscript"
#    FITIMAGE_IMAGE_bootscript[type] ?= "bootscript"
#    FITIMAGE_IMAGE_bootscript[file] ?= "boot.scr"
#
# Valid options for the [type] varflag are: "kernel", "fdt", "fdto", "fdtapply", "ramdisk", "bootscript".
#
# To enable signing, set
#
#    FITIMAGE_SIGN = "1"
#
# and configure FITIMAGE_SIGN_KEYDIR (and FITIMAGE_SIGN_KEYNAME) according to
# your needs.
#
# For signing via PKCS#11 URIs provided by the meta-oe signing.bbclass, add:
#
#    inherit signing
#
#    FITIMAGE_SIGNING_KEY_ROLE = "fit"
#
#    do_fitimage:prepend() {
#        signing_prepare
#        signing_use_role "${FITIMAGE_SIGNING_KEY_ROLE}"
#    }
#
#    FITIMAGE_SIGN = "1"
#    FITIMAGE_MKIMAGE_EXTRA_ARGS = "--engine pkcs11"
#    FITIMAGE_SIGN_KEYDIR = "${PKCS11_URI}"


LICENSE ?= "MIT"

inherit deploy kernel-artifact-names image-artifact-names kernel-arch nopackages

do_patch[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
deltask do_populate_sysroot

INHIBIT_DEFAULT_DEPS = "1"

DEPENDS = "u-boot-mkimage-native dtc-native"

FITIMAGE_SIGN ?= "0"
FITIMAGE_SIGN[doc] = "Enable FIT image signing"
FITIMAGE_SIGN_KEYDIR ?= ""
FITIMAGE_SIGN_KEYDIR[doc] = "Key directory or pkcs#11 URI to use for signing configuration"
FITIMAGE_MKIMAGE_EXTRA_ARGS[doc] = "Extra arguemnts to pass to uboot-mkimage call"
FITIMAGE_HASH_ALGO ?= "sha256"
FITIMAGE_HASH_ALGO[doc] = "Hash algorithm to use"
FITIMAGE_ENCRYPT_ALGO ?= "rsa2048"
FITIMAGE_ENCRYPT_ALGO[doc] = "Signature algorithm to use"
FITIMAGE_CONFIG_PREFIX ?= "conf-"
FITIMAGE_CONFIG_PREFIX[doc] = "Prefix to use for FIT configuration node name"

FITIMAGE_LOADADDRESS ??= ""
FITIMAGE_ENTRYPOINT  ??= ""
FITIMAGE_DTB_LOADADDRESS ??= ""
FITIMAGE_DTB_OVERLAY_LOADADDRESS ??= ""
FITIMAGE_RD_LOADADDRESS ??= ""
FITIMAGE_RD_ENTRYPOINT ??= ""

PACKAGE_ARCH = "${MACHINE_ARCH}"

# Create dependency list from images
python __anonymous() {
    for image in (d.getVar('FITIMAGE_IMAGES') or "").split():
        imageflags = d.getVarFlags('FITIMAGE_IMAGE_%s' % image, expand=['type', 'depends']) or {}
        imgtype = imageflags.get('type')
        if not imgtype:
            bb.debug(1, "No [type] given for image '%s', defaulting to 'kernel'" % image)
            imgtype = 'kernel'
        recipe = d.getVar('FITIMAGE_IMAGE_%s' % image)

        if not recipe:
            bb.error("No recipe set for image '%s'. Specify via 'FITIMAGE_IMAGE_%s = \"<recipe-name>\"'" % (recipe, image))
            return

        d.appendVarFlag('do_unpack', 'vardeps', ' FITIMAGE_IMAGE_%s' % image)
        depends = imageflags.get('depends')
        if depends:
            d.appendVarFlag('do_unpack', 'depends', ' ' + depends)
            continue

        if imgtype == 'ramdisk':
            d.appendVarFlag('do_unpack', 'depends', ' ' + recipe + ':do_image_complete')
        elif 'fdt' in imgtype:
            d.appendVarFlag('do_unpack', 'depends', ' ' + recipe + ':do_populate_sysroot')
            d.appendVarFlag('do_unpack', 'depends', ' ' + recipe + ':do_deploy')
        else:
            d.appendVarFlag('do_unpack', 'depends', ' ' + recipe + ':do_deploy')

        if 'fdt' in imgtype and d.getVar('PREFERRED_PROVIDER_virtual/dtb'):
            d.setVar('EXTERNAL_KERNEL_DEVICETREE', '${RECIPE_SYSROOT}/boot/devicetree')
}

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
B = "${WORKDIR}/build"

#
# Emit the fitImage ITS header
#
def fitimage_emit_fit_header(d, fd):
    fd.write('/dts-v1/;\n\n/ {\n')
    fd.write(d.expand('\tdescription = "fitImage for ${DISTRO_NAME}/${PV}/${MACHINE}";\n'))
    fd.write('\t#address-cells = <1>;\n')

#
# Emit the fitImage ITS footer
#
def fitimage_emit_fit_footer(d, fd):
    fd.write('};\n')

#
# Emit the fitImage section
#
def fitimage_emit_section_start(d, fd, section):
    fd.write(f'\t{section} {{\n')

#
# Emit the fitImage section end
#
def fitimage_emit_section_end(d, fd):
    fd.write('\t};\n')

def fitimage_emit_section_kernel(d, fd, imgpath, imgsource, imgcomp):
    kernelcount = 1
    kernel_csum = d.getVar("FITIMAGE_HASH_ALGO")
    arch = d.getVar("ARCH")
    loadaddr = d.getVar("FITIMAGE_LOADADDRESS")
    entryaddr = d.getVar("FITIMAGE_ENTRYPOINT")

    bb.note(f"Adding kernel-{kernelcount} section to ITS file")

    fd.write(f'\t\tkernel-{kernelcount} {{\n')
    fd.write('\t\t\tdescription = "Linux kernel";\n')
    fd.write(f'\t\t\tdata = /incbin/("{imgpath}/{imgsource}");\n')
    fd.write('\t\t\ttype = "kernel";\n')
    fd.write(f'\t\t\tarch = "{arch}";\n')
    fd.write('\t\t\tos = "linux";\n')
    fd.write(f'\t\t\tcompression = "{imgcomp}";\n')
    if (loadaddr):
        fd.write(f'\t\t\tload = <{loadaddr}>;\n')
    if (entryaddr):
        fd.write(f'\t\t\tentry = <{entryaddr}>;\n')
    fd.write('\t\t\thash-1 {\n')
    fd.write(f'\t\t\t\talgo = "{kernel_csum}";\n')
    fd.write('\t\t\t};\n')
    fd.write('\t\t};\n')

#
# Emit the fitImage ITS DTB section
#
def _fitimage_emit_section_dtb(d, fd, dtb_file, dtb_path, loadaddr, desc):
    dtb_csum = d.getVar("FITIMAGE_HASH_ALGO")
    arch = d.getVar("ARCH")

    bb.note(f"Adding fdt-{dtb_file} section to ITS file")

    fd.write(f'\t\tfdt-{dtb_file} {{\n')
    fd.write(f'\t\t\tdescription = "{desc}";\n')
    fd.write(f'\t\t\tdata = /incbin/("{dtb_path}/{dtb_file}");\n')
    fd.write('\t\t\ttype = "flat_dt";\n')
    fd.write(f'\t\t\tarch = "{arch}";\n')
    fd.write('\t\t\tcompression = "none";\n')
    if loadaddr:
        fd.write(f'\t\t\tload = <{loadaddr}>;\n')
    fd.write('\t\t\thash-1 {\n')
    fd.write(f'\t\t\t\talgo = "{dtb_csum}";\n')
    fd.write('\t\t\t};\n')
    fd.write('\t\t};\n')


def fitimage_emit_section_dtb(d, fd, dtb_file, dtb_path):
    loadaddr = d.getVar("FITIMAGE_DTB_LOADADDRESS")

    _fitimage_emit_section_dtb(d, fd, dtb_file, dtb_path, loadaddr, "Flattened Device Tree blob")

#
# Emit the fitImage ITS DTB overlay section
#
def fitimage_emit_section_dtb_overlay(d, fd, dtb_file, dtb_path):
    loadaddr = d.getVar("FITIMAGE_DTB_OVERLAY_LOADADDRESS")

    _fitimage_emit_section_dtb(d, fd, dtb_file, dtb_path, loadaddr, "Flattened Device Tree Overlay blob")


#
# Emit the fitImage ITS ramdisk section
#
def fitimage_emit_section_ramdisk(d, fd, img_file, img_path):
    ramdisk_count = "1"
    ramdisk_csum = d.getVar("FITIMAGE_HASH_ALGO")
    arch = d.getVar("ARCH")
    loadaddr = d.getVar("FITIMAGE_RD_LOADADDRESS")
    entryaddr = d.getVar("FITIMAGE_RD_ENTRYPOINT")

    bb.note(f"Adding ramdisk-{ramdisk_count} section to ITS file")

    fd.write(f'\t\tramdisk-{ramdisk_count} {{\n')
    fd.write(f'\t\t\tdescription = "{img_file}";\n')
    fd.write(f'\t\t\tdata = /incbin/("{img_path}/{img_file}");\n')
    fd.write('\t\t\ttype = "ramdisk";\n')
    fd.write(f'\t\t\tarch = "{arch}";\n')
    fd.write('\t\t\tos = "linux";\n')
    fd.write('\t\t\tcompression = "none";\n')
    if (loadaddr):
        fd.write(f'\t\t\tload = <{loadaddr}>;\n')
    if (entryaddr):
        fd.write(f'\t\t\tentry = <{entryaddr}>;\n')
    fd.write('\t\t\thash-1 {\n')
    fd.write(f'\t\t\t\talgo = "{ramdisk_csum}";\n')
    fd.write('\t\t\t};\n')
    fd.write('\t\t};\n')

def fitimage_emit_section_bootscript(d, fd, imgpath, imgsource):
    hash_algo = d.getVar("FITIMAGE_HASH_ALGO")
    arch = d.getVar("ARCH")

    bb.note(f"Adding bootscr-{imgsource} section to ITS file")

    fd.write(f'\t\tbootscr-{imgsource} {{\n')
    fd.write('\t\t\tdescription = "U-boot script";\n')
    fd.write(f'\t\t\tdata = /incbin/("{imgpath}/{imgsource}");\n')
    fd.write('\t\t\ttype = "script";\n')
    fd.write(f'\t\t\tarch = "{arch}";\n')
    fd.write('\t\t\tos = "linux";\n')
    fd.write('\t\t\tcompression = "none";\n')
    fd.write('\t\t\thash-1 {\n')
    fd.write(f'\t\t\t\talgo = "{hash_algo}";\n')
    fd.write('\t\t\t};\n')
    fd.write('\t\t};\n')

def fitimage_emit_subsection_signature(d, fd, sign_images_list):
    hash_algo = d.getVar("FITIMAGE_HASH_ALGO")
    encrypt_algo = d.getVar("FITIMAGE_ENCRYPT_ALGO") or ""
    conf_sign_keyname = d.getVar("FITIMAGE_SIGN_KEYNAME")
    signer_name = d.getVar("FITIMAGE_SIGNER")
    signer_version = d.getVar("FITIMAGE_SIGNER_VERSION")
    sign_images = ", ".join(f'"{s}"' for s in sign_images_list)

    fd.write('\t\t\tsignature-1 {\n')
    fd.write(f'\t\t\t\talgo = "{hash_algo},{encrypt_algo}";\n')
    if conf_sign_keyname:
        fd.write(f'\t\t\t\tkey-name-hint = {conf_sign_keyname}";\n')
    fd.write(f'\t\t\t\tsign-images = {sign_images};\n')
    fd.write(f'\t\t\t\tsigner-name = "{signer_name}";\n')
    fd.write(f'\t\t\t\tsigner-version = "{signer_version}";\n')
    fd.write('\t\t\t};\n')

#
# Emit the fitImage ITS configuration section
#
def fitimage_emit_section_config(d, fd, dtb, kernelcount, ramdiskcount, setupcount, bootscriptid, compatible, dtbcount):
    sign = d.getVar("FITIMAGE_SIGN")
    conf_default = None
    conf_prefix = d.getVar('FITIMAGE_CONFIG_PREFIX') or ""

    bb.note(f"Adding {dtb} section to ITS file")

    conf_desc="Linux kernel"
    if dtb:
        conf_desc += ", FDT blob"
    if ramdiskcount:
         conf_desc += ", ramdisk"
    if setupcount:
         conf_desc += ", setup"
    if bootscriptid:
         conf_desc += ", u-boot script"
    if dtbcount == 1:
        conf_default = d.getVar('FITIMAGE_DEFAULT_CONFIG') or f'{conf_prefix}{dtb}'

    if conf_default:
        fd.write(f'\t\tdefault = "{conf_default}";\n')
    fd.write(f'\t\t{conf_prefix}{dtb} {{\n')
    fd.write(f'\t\t\tdescription = "{dtbcount} {conf_desc}";\n')
    if kernelcount:
        fd.write('\t\t\tkernel = "kernel-1";\n')
    fd.write(f'\t\t\tfdt = "fdt-{dtb}";\n')
    if ramdiskcount:
        fd.write(f'\t\t\tramdisk = "ramdisk-{ramdiskcount}";\n')
    if bootscriptid:
        fd.write(f'\t\t\tbootscr = "bootscr-{bootscriptid}";\n')
    if compatible:
        fd.write(f'\t\t\tcompatible = "{compatible}";\n')

    if sign == "1":
        sign_images = ["kernel"]
        if dtb:
            sign_images.append("fdt")
        if ramdiskcount:
            sign_images.append("ramdisk")
        if setupcount:
            sign_images.append("setup")
        if bootscriptid:
            sign_images.append("bootscr")
        fitimage_emit_subsection_signature(d, fd, sign_images)

    fd.write('\t\t'  + '};\n')

#
# Emits a device tree overlay config section
#
def fitimage_emit_section_config_fdto(d, fd, dtb, compatible):
    sign = d.getVar("FITIMAGE_SIGN")
    bb.note("Adding overlay config section to ITS file")

    fd.write(f'\t\t{dtb} {{\n')
    fd.write(f'\t\t\tdescription = "Device Tree Overlay";\n')
    fd.write(f'\t\t\tfdt = "fdt-{dtb}";')
    if compatible:
       fd.write(f'\t\t\tcompatible = "{compatible}";')

    if sign == "1":
        sign_images = ["fdt"]
        fitimage_emit_subsection_signature(d, fd, sign_images)

    fd.write('\t\t'  + '};\n')

python write_manifest() {
    machine = d.getVar('MACHINE')
    kernelcount=1
    DTBS = ""
    DTBOS = ""
    ramdiskcount = ""
    setupcount = ""
    bootscriptid = ""
    compatible = ""

    def get_dtbs(d, dtb_suffix):
        sysroot = d.getVar('RECIPE_SYSROOT')
        deploydir = d.getVar('DEPLOY_DIR_IMAGE')

        dtbs = (d.getVar('KERNEL_DEVICETREE') or '').split()
        dtbs = [os.path.basename(x) for x in dtbs if x.endswith(dtb_suffix)]
        ext_dtbs = os.listdir(d.getVar('EXTERNAL_KERNEL_DEVICETREE')) if d.getVar('EXTERNAL_KERNEL_DEVICETREE') else []
        ext_dtbs = [x for x in ext_dtbs if x.endswith(dtb_suffix)]

        result = []
        # Prefer BSP dts if BSP and kernel provide the same dts
        for d in sorted(set(dtbs + ext_dtbs)):
            dtbpath = f'{sysroot}/boot/devicetree/{d}' if d in ext_dtbs else f'{deploydir}/{d}'
            result.append(dtbpath)

        return " ".join(result)

    with open('%s/manifest.its' % d.getVar('B'), 'w') as fd:
        images = d.getVar('FITIMAGE_IMAGES')
        if not images:
            bb.warn("No images specified in FITIMAGE_IMAGES. Generated FIT image will be empty")

        fitimage_emit_fit_header(d, fd)
        fitimage_emit_section_start(d, fd, 'images')

        for image in (images or "").split():
            imageflags = d.getVarFlags('FITIMAGE_IMAGE_%s' % image, expand=['file', 'fstype', 'type', 'comp']) or {}
            imgtype = imageflags.get('type', '')
            if imgtype == 'kernel':
                default = "%s-%s%s" % (d.getVar('KERNEL_IMAGETYPE'), machine, d.getVar('KERNEL_IMAGE_BIN_EXT'))
                imgsource = imageflags.get('file', default)
                imgcomp = imageflags.get('comp', 'none')
                imgpath = d.getVar("DEPLOY_DIR_IMAGE")
                fitimage_emit_section_kernel(d, fd, imgpath, imgsource, imgcomp)
            elif imgtype == 'fdt':
                default = get_dtbs(d, "dtb")
                dtbfiles = imageflags.get('file', default)
                if not dtbfiles:
                    bb.fatal(f"No dtb file found for image '{image}'. Set KERNEL_DEVICETREE, [file] varflag, or reference devicetree.bbclass-based recipe.")
                for dtb in dtbfiles.split():
                    dtb_path, dtb_file = os.path.split(dtb)
                    DTBS += f" {dtb}"
                    fitimage_emit_section_dtb(d, fd, dtb_file, dtb_path)
            elif imgtype == 'fdto':
                default = get_dtbs(d, "dtbo")
                dtbofiles = imageflags.get('file', default)
                if not dtbofiles:
                    bb.fatal(f"No dtbo file found for image '{image}'. Set KERNEL_DEVICETREE, [file] varflag, or reference devicetree.bbclass-based recipe.")
                for dtb in dtbofiles.split():
                    dtb_path, dtb_file = os.path.split(dtb)
                    DTBOS = DTBOS + " " + dtb
                    fitimage_emit_section_dtb_overlay(d, fd, dtb_file, dtb_path)
            elif imgtype == 'fdtapply':
                import subprocess
                dtbofiles = imageflags.get('file', None)
                if not dtbofiles:
                    bb.fatal(f"No dtbo file found for image '{image}'. Set via [file] varflag.")
                dtboutname = imageflags.get('name', None)
                if not dtboutname:
                    bb.fatal(f"No dtb output name found for image '{image}'. Set via [name] varflag.")
                dtbresult = "%s/%s" % (d.getVar('B'), dtboutname)
                dtbcommand = ""
                for dtb in dtbofiles.split():
                    dtb_path, dtb_file = os.path.split(dtb)
                    if not dtb_path:
                        dtb_path = d.getVar("DEPLOY_DIR_IMAGE")
                    if not dtbcommand:
                        if not dtb_file.endswith('.dtb'):
                            bb.fatal(f"fdtapply failed: Expected (non-overlay) .dtb file as first element, but got {dtb_file}")
                        dtbcommand = f"fdtoverlay -i {dtb_path}/{dtb_file} -o {dtbresult}"
                    else:
                        if not dtb_file.endswith('.dtbo'):
                            bb.fatal(f"fdtapply failed: Expected .dtbo file, but got {dtb_file}")
                        dtbcommand += f" {dtb_path}/{dtb_file}"
                result = subprocess.run(dtbcommand, stderr=subprocess.PIPE, shell=True, text=True)
                if result.returncode != 0:
                    bb.fatal(f"Running {dtbcommand} failed: {result.stderr}")
                dtb_path, dtb_file = os.path.split(dtbresult)
                DTBS += f" {dtbresult}"
                fitimage_emit_section_dtb(d, fd, dtb_file, dtb_path)
            elif imgtype == 'ramdisk':
                ramdiskcount = "1"
                default_imgfstype = d.getVar('INITRAMFS_FSTYPES' or "").split()[0]
                img_fstype = imageflags.get('fstype', default_imgfstype)
                img_file = "%s%s.%s" % (d.getVar('FITIMAGE_IMAGE_%s' % image), d.getVar('IMAGE_MACHINE_SUFFIX'), img_fstype)
                img_path = d.getVar("DEPLOY_DIR_IMAGE")
                fitimage_emit_section_ramdisk(d, fd, img_file, img_path)
            elif imgtype == 'bootscript':
                if bootscriptid:
                    bb.fatal("Only a single boot script is supported (already set to: %s)" % bootscriptid)
                imgsource = imageflags.get('file', None)
                imgpath = d.getVar("DEPLOY_DIR_IMAGE")
                bootscriptid = imgsource
                fitimage_emit_section_bootscript(d, fd, imgpath, imgsource)
        fitimage_emit_section_end(d, fd)
        #
        # Step 5: Prepare a configurations section
        #
        fitimage_emit_section_start(d, fd, 'configurations')
        dtbcount = 1
        for dtb in (DTBS or "").split():
            import subprocess
            try:
                cmd = "fdtget -t s {} / compatible".format(dtb)
                compatible = subprocess.check_output(cmd, shell=True, text=True).split()[0]
            except subprocess.CalledProcessError:
                bb.fatal("Failed to find root-node compatible string in (%s)" % dtb)

            dtb_path, dtb_file = os.path.split(dtb)
            fitimage_emit_section_config(d, fd, dtb_file, kernelcount, ramdiskcount, setupcount, bootscriptid, compatible, dtbcount)
            dtbcount += 1
        for dtb in (DTBOS or "").split():
            import subprocess
            try:
                cmd = "fdtget -t s {} / compatible".format(dtb)
                compatible = subprocess.check_output(cmd, shell=True, text=True).split()[0]
            except subprocess.CalledProcessError:
                bb.note("Failed to find root-node compatible string in (%s)" % dtb)
                compatible = None

            dtb_path, dtb_file = os.path.split(dtb)
            fitimage_emit_section_config_fdto(d, fd, dtb_file, compatible)

        fitimage_emit_section_end(d, fd)
        fitimage_emit_fit_footer(d, fd)
}

do_configure[postfuncs] += "write_manifest"

do_fitimage () {
    if [ "${FITIMAGE_SIGN}" = "1" ]; then
        uboot-mkimage ${FITIMAGE_MKIMAGE_EXTRA_ARGS} \
            -k ${FITIMAGE_SIGN_KEYDIR} -r \
            -f "${B}/manifest.its" \
            "${B}/fitImage"
    else
        uboot-mkimage ${FITIMAGE_MKIMAGE_EXTRA_ARGS} \
            -f "${B}/manifest.its" \
            "${B}/fitImage"
    fi
}
addtask fitimage after do_configure

ITS_NAME ?= "${PN}-${KERNEL_ARTIFACT_NAME}"
ITS_LINK_NAME ?= "${PN}-${KERNEL_ARTIFACT_LINK_NAME}"
FITIMAGE_IMAGE_NAME ?= "fitImage-${PN}-${KERNEL_FIT_NAME}${KERNEL_FIT_BIN_EXT}"
FITIMAGE_IMAGE_LINK_NAME ?= "fitImage-${PN}-${KERNEL_FIT_LINK_NAME}"

SSTATE_SKIP_CREATION:task-deploy = '1'

do_deploy() {
    bbnote 'Copying fit-image.its source file...'
    install -m 0644 ${B}/manifest.its ${DEPLOYDIR}/${ITS_NAME}.its

    bbnote 'Copying all created fdt from type fdtapply'
    for DTB_FILE in `find ${B} -maxdepth 1 -name *.dtb`; do
        install -m 0644 ${DTB_FILE} ${DEPLOYDIR}/
    done

    bbnote 'Copying fitImage file...'
    install -m 0644 ${B}/fitImage ${DEPLOYDIR}/${FITIMAGE_IMAGE_NAME}

    cd ${DEPLOYDIR}
    ln -sf ${ITS_NAME}.its ${ITS_LINK_NAME}.its
    ln -sf ${FITIMAGE_IMAGE_NAME} ${FITIMAGE_IMAGE_LINK_NAME}
}
addtask deploy after do_fitimage before do_build
