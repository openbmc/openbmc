inherit pkgconfig cmake

LICENSE = "Proprietary"
LICFILENAME = "license.txt"
LIC_FILES_CHKSUM = "file://${S}/${LICFILENAME};md5=39ab6ab638f4d1836ba994ec6852de94"

# We should move to an actual SRCREV eventually
include conf/xilinx/esw-srcrev.inc

SRCREV ?= "${AUTOREV}"
PV = "${XILINX_RELEASE_VERSION}+git${SRCPV}"
REPO ?= "git://github.com/xilinx/embeddedsw.git;protocol=https"
BRANCH ?= "master"
SRC_URI = "${REPO};branch=${BRANCH}"

SRCREV_FORMAT = "src_decouple"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"
OECMAKE_SOURCEPATH = "${S}/${ESW_COMPONENT_SRC}"

SPECFILE_PATH_arm = "${S}/scripts/specs/arm/Xilinx.spec"
SPECFILE_PATH_aarch64 = "${S}/scripts/specs/arm/Xilinx.spec"
SPECFILE_PATH_microblaze = "${S}/scripts/specs/microblaze/Xilinx.spec"

ESW_CFLAGS += "-specs=${SPECFILE_PATH}"

inherit ccmake

COMPATIBLE_HOST = ".*-elf"
COMPATIBLE_HOST_arm = "[^-]*-[^-]*-eabi"

DTBFILE ?= "${RECIPE_SYSROOT}/boot/devicetree/system-top.dtb"

def get_xlnx_cmake_machine(fam, d):
    cmake_machine = fam
    if (fam == 'zynqmp'):
        cmake_machine = 'ZynqMP'
    elif (fam == 'versal'):
        cmake_machine = 'Versal'
    elif (fam == 'zynq'):
        cmake_machine = 'Zynq'
    return cmake_machine

def get_xlnx_cmake_processor(tune, machine, d):
    cmake_processor = tune
    if tune.startswith('microblaze'):
        if (machine == 'microblaze-pmu'):
            cmake_processor = 'pmu_microblaze'
        elif (machine == 'microblaze-plm'):
            cmake_processor = 'plm_microblaze'
        else:
            cmake_processor = 'microblaze'
    elif (tune in [ 'cortexr5', 'cortexr5f' ]):
        cmake_processor = 'cortexr5'
    elif tune.startswith('cortexa9'):
        cmake_processor = 'cortexa9'
    elif (tune in [ 'cortexa53', 'cortexa72-cortexa53' ]):
        cmake_processor = 'cortexa53'
    elif tune == 'cortexa72':
        cmake_processor = 'cortexa72'
    return cmake_processor

XLNX_CMAKE_MACHINE = "${@get_xlnx_cmake_machine(d.getVar('SOC_FAMILY'), d)}"
XLNX_CMAKE_PROCESSOR = "${@get_xlnx_cmake_processor(d.getVar('DEFAULTTUNE'), d.getVar('MACHINE'), d)}"
XLNX_CMAKE_SYSTEM_NAME ?= "Generic"

cmake_do_generate_toolchain_file_append() {
    cat >> ${WORKDIR}/toolchain.cmake <<EOF
    include(CMakeForceCompiler)
    CMAKE_FORCE_C_COMPILER("${OECMAKE_C_COMPILER}" GNU)
    CMAKE_FORCE_CXX_COMPILER("${OECMAKE_CXX_COMPILER}" GNU)
    set( CMAKE_SYSTEM_PROCESSOR "${XLNX_CMAKE_PROCESSOR}" )
    set( CMAKE_MACHINE "${XLNX_CMAKE_MACHINE}" )
    # Will need this in the future to make cmake understand esw variables
    # set( CMAKE_SYSTEM_NAME `echo elf | sed -e 's/^./\u&/' -e 's/^\(Linux\).*/\1/'` )
    set( CMAKE_SYSTEM_NAME "${XLNX_CMAKE_SYSTEM_NAME}" )
EOF
}

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -m 0755  ${B}/${ESW_COMPONENT_NAME} ${D}${libdir}
    install -m 0644  ${B}/include/*.h ${D}${includedir}
}

CFLAGS_append = " ${ESW_CFLAGS}"

# We need to find the license file, which vaires depending on the component
# recurse a maximum of x times, could be fancier but it gets complicated since
# we dont know for certain we are running devtool or just externalsrc
python(){
    import os.path
    if bb.data.inherits_class('externalsrc', d) and d.getVar('EXTERNALSRC'):
        externalsrc = d.getVar('EXTERNALSRC')
        lic_file = d.getVar('LIC_FILES_CHKSUM', False)
        licpath=externalsrc
        for i in range(5):
            if os.path.isfile(licpath + '/' + d.getVar('LICFILENAME',True)):
                lic_file = lic_file.replace('${S}',licpath)
                d.setVar('LIC_FILES_CHKSUM', lic_file)
                return
            licpath=os.path.dirname(licpath)
        bb.error("Couldn't find license file: %s, within directory %s or his parent directories" % (d.getVar('LICFILENAME',True), externalsrc))
}
