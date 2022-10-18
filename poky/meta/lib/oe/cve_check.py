import collections
import re
import itertools
import functools

_Version = collections.namedtuple(
    "_Version", ["release", "patch_l", "pre_l", "pre_v"]
)

@functools.total_ordering
class Version():

    def __init__(self, version, suffix=None):

        suffixes = ["alphabetical", "patch"]

        if str(suffix) == "alphabetical":
            version_pattern =  r"""r?v?(?:(?P<release>[0-9]+(?:[-\.][0-9]+)*)(?P<patch>[-_\.]?(?P<patch_l>[a-z]))?(?P<pre>[-_\.]?(?P<pre_l>(rc|alpha|beta|pre|preview|dev))[-_\.]?(?P<pre_v>[0-9]+)?)?)(.*)?"""
        elif str(suffix) == "patch":
            version_pattern =  r"""r?v?(?:(?P<release>[0-9]+(?:[-\.][0-9]+)*)(?P<patch>[-_\.]?(p|patch)(?P<patch_l>[0-9]+))?(?P<pre>[-_\.]?(?P<pre_l>(rc|alpha|beta|pre|preview|dev))[-_\.]?(?P<pre_v>[0-9]+)?)?)(.*)?"""
        else:
            version_pattern =  r"""r?v?(?:(?P<release>[0-9]+(?:[-\.][0-9]+)*)(?P<pre>[-_\.]?(?P<pre_l>(rc|alpha|beta|pre|preview|dev))[-_\.]?(?P<pre_v>[0-9]+)?)?)(.*)?"""
        regex = re.compile(r"^\s*" + version_pattern + r"\s*$", re.VERBOSE | re.IGNORECASE)

        match = regex.search(version)
        if not match:
            raise Exception("Invalid version: '{0}'".format(version))

        self._version = _Version(
            release=tuple(int(i) for i in match.group("release").replace("-",".").split(".")),
            patch_l=match.group("patch_l") if str(suffix) in suffixes and match.group("patch_l") else "",
            pre_l=match.group("pre_l"),
            pre_v=match.group("pre_v")
        )

        self._key = _cmpkey(
            self._version.release,
            self._version.patch_l,
            self._version.pre_l,
            self._version.pre_v
        )

    def __eq__(self, other):
        if not isinstance(other, Version):
            return NotImplemented
        return self._key == other._key

    def __gt__(self, other):
        if not isinstance(other, Version):
            return NotImplemented
        return self._key > other._key

def _cmpkey(release, patch_l, pre_l, pre_v):
    # remove leading 0
    _release = tuple(
        reversed(list(itertools.dropwhile(lambda x: x == 0, reversed(release))))
    )

    _patch = patch_l.upper()

    if pre_l is None and pre_v is None:
        _pre = float('inf')
    else:
        _pre = float(pre_v) if pre_v else float('-inf')
    return _release, _patch, _pre

def cve_check_merge_jsons(output, data):
    """
    Merge the data in the "package" property to the main data file
    output
    """
    if output["version"] != data["version"]:
        bb.error("Version mismatch when merging JSON outputs")
        return

    for product in output["package"]:
        if product["name"] == data["package"][0]["name"]:
            bb.error("Error adding the same package twice")
            return

    output["package"].append(data["package"][0])

def update_symlinks(target_path, link_path):
    """
    Update a symbolic link link_path to point to target_path.
    Remove the link and recreate it if exist and is different.
    """
    if link_path != target_path and os.path.exists(target_path):
        if os.path.exists(os.path.realpath(link_path)):
            os.remove(link_path)
        os.symlink(os.path.basename(target_path), link_path)

def get_patched_cves(d):
    """
    Get patches that solve CVEs using the "CVE: " tag.
    """

    import re
    import oe.patch

    pn = d.getVar("PN")
    cve_match = re.compile("CVE:( CVE\-\d{4}\-\d+)+")

    # Matches the last "CVE-YYYY-ID" in the file name, also if written
    # in lowercase. Possible to have multiple CVE IDs in a single
    # file name, but only the last one will be detected from the file name.
    # However, patch files contents addressing multiple CVE IDs are supported
    # (cve_match regular expression)

    cve_file_name_match = re.compile(".*([Cc][Vv][Ee]\-\d{4}\-\d+)")

    patched_cves = set()
    bb.debug(2, "Looking for patches that solves CVEs for %s" % pn)
    for url in oe.patch.src_patches(d):
        patch_file = bb.fetch.decodeurl(url)[2]

        # Remote compressed patches may not be unpacked, so silently ignore them
        if not os.path.isfile(patch_file):
            bb.warn("%s does not exist, cannot extract CVE list" % patch_file)
            continue

        # Check patch file name for CVE ID
        fname_match = cve_file_name_match.search(patch_file)
        if fname_match:
            cve = fname_match.group(1).upper()
            patched_cves.add(cve)
            bb.debug(2, "Found CVE %s from patch file name %s" % (cve, patch_file))

        with open(patch_file, "r", encoding="utf-8") as f:
            try:
                patch_text = f.read()
            except UnicodeDecodeError:
                bb.debug(1, "Failed to read patch %s using UTF-8 encoding"
                        " trying with iso8859-1" %  patch_file)
                f.close()
                with open(patch_file, "r", encoding="iso8859-1") as f:
                    patch_text = f.read()

        # Search for one or more "CVE: " lines
        text_match = False
        for match in cve_match.finditer(patch_text):
            # Get only the CVEs without the "CVE: " tag
            cves = patch_text[match.start()+5:match.end()]
            for cve in cves.split():
                bb.debug(2, "Patch %s solves %s" % (patch_file, cve))
                patched_cves.add(cve)
                text_match = True

        if not fname_match and not text_match:
            bb.debug(2, "Patch %s doesn't solve CVEs" % patch_file)

    return patched_cves


def get_cpe_ids(cve_product, version):
    """
    Get list of CPE identifiers for the given product and version
    """

    version = version.split("+git")[0]

    cpe_ids = []
    for product in cve_product.split():
        # CVE_PRODUCT in recipes may include vendor information for CPE identifiers. If not,
        # use wildcard for vendor.
        if ":" in product:
            vendor, product = product.split(":", 1)
        else:
            vendor = "*"

        cpe_id = 'cpe:2.3:a:{}:{}:{}:*:*:*:*:*:*:*'.format(vendor, product, version)
        cpe_ids.append(cpe_id)

    return cpe_ids
