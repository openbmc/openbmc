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
