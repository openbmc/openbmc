#This class applies patches to an XML file during do_patch().  The
#patches themselves are specified in XML in a separate file that must
#be in SRC_URI and end in .patch.xml.  The patch XML file must also
#have a <targetFile> element that specifies the base name of the file
#that needs to be patched.
#
#See patchxml.py for details on the XML patch format.
#

inherit python3native
inherit obmc-phosphor-utils
do_patch[depends] = "mrw-patch-native:do_populate_sysroot"


def find_patch_files(d):
    all_patches = listvar_to_list(d, 'SRC_URI')
    xml_patches = [x for x in all_patches if x.endswith('.patch.xml') and
                         x.startswith('file://')]

    return [x.lstrip('file://') for x in xml_patches]


def apply_xml_patch(base_patch_name, d):
    import xml.etree.ElementTree as et
    import subprocess

    patch_file = os.path.join(d.getVar("WORKDIR", True), base_patch_name)

    if not os.path.exists(patch_file):
        bb.fatal("Could not find patch file " + patch_file +
                 " specified in SRC_URI")

    patch_tree = et.parse(patch_file)
    patch_root = patch_tree.getroot()
    target_file_element = patch_root.find("targetFile")

    if target_file_element is None:
        bb.fatal("Could not find <targetFile> element in patch file "
                 + patch_file)
    else:
        xml = target_file_element.text

    xml = os.path.join(d.getVar("S", True), xml)

    if not os.path.exists(xml):
        bb.fatal("Could not find XML file to patch: " + xml)

    print("Applying XML fixes found in " + patch_file + " to " + xml)

    cmd = []
    cmd.append(os.path.join(d.getVar("bindir", True), "obmc-mrw/patchxml.py"))
    cmd.append("-x")
    cmd.append(xml)
    cmd.append("-p")
    cmd.append(patch_file)
    cmd.append("-o")
    cmd.append(xml + ".patched")

    try:
        subprocess.check_call(cmd)
    except subprocess.CalledProcessError as e:
        bb.fatal("Failed patching XML:\n%s" % e.output)

    os.rename(xml, xml + ".orig")
    os.rename(xml + ".patched", xml)


python xmlpatch_do_patch() {

    for patch in find_patch_files(d):
        apply_xml_patch(patch, d)
}

do_patch[postfuncs] += "xmlpatch_do_patch"
