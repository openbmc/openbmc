FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# Directory with new layer of source tree with additional files
YAML_DIR = "xyz"

SRC_URI += "file://${YAML_DIR}"

S = "${WORKDIR}/git"

# Merge source tree by original project with our layer of additional files
do_add_yamls(){
    cp -r "${WORKDIR}/${YAML_DIR}" "${S}"
}

addtask do_add_yamls after do_unpack before do_configure
