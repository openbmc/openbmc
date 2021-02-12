"""
BitBake 'Fetch' implementations

Classes for obtaining upstream sources for the
BitBake build tools.
"""

# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2012  Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import os, re
import signal
import logging
import urllib.request, urllib.parse, urllib.error
if 'git' not in urllib.parse.uses_netloc:
    urllib.parse.uses_netloc.append('git')
import operator
import collections
import subprocess
import pickle
import errno
import bb.persist_data, bb.utils
import bb.checksum
import bb.process
import bb.event

__version__ = "2"
_checksum_cache = bb.checksum.FileChecksumCache()

logger = logging.getLogger("BitBake.Fetcher")

CHECKSUM_LIST = [ "md5", "sha256", "sha1", "sha384", "sha512" ]
SHOWN_CHECKSUM_LIST = ["sha256"]

class BBFetchException(Exception):
    """Class all fetch exceptions inherit from"""
    def __init__(self, message):
        self.msg = message
        Exception.__init__(self, message)

    def __str__(self):
        return self.msg

class UntrustedUrl(BBFetchException):
    """Exception raised when encountering a host not listed in BB_ALLOWED_NETWORKS"""
    def __init__(self, url, message=''):
        if message:
            msg = message
        else:
            msg = "The URL: '%s' is not trusted and cannot be used" % url
        self.url = url
        BBFetchException.__init__(self, msg)
        self.args = (url,)

class MalformedUrl(BBFetchException):
    """Exception raised when encountering an invalid url"""
    def __init__(self, url, message=''):
        if message:
            msg = message
        else:
            msg = "The URL: '%s' is invalid and cannot be interpreted" % url
        self.url = url
        BBFetchException.__init__(self, msg)
        self.args = (url,)

class FetchError(BBFetchException):
    """General fetcher exception when something happens incorrectly"""
    def __init__(self, message, url = None):
        if url:
            msg = "Fetcher failure for URL: '%s'. %s" % (url, message)
        else:
            msg = "Fetcher failure: %s" % message
        self.url = url
        BBFetchException.__init__(self, msg)
        self.args = (message, url)

class ChecksumError(FetchError):
    """Exception when mismatched checksum encountered"""
    def __init__(self, message, url = None, checksum = None):
        self.checksum = checksum
        FetchError.__init__(self, message, url)

class NoChecksumError(FetchError):
    """Exception when no checksum is specified, but BB_STRICT_CHECKSUM is set"""

class UnpackError(BBFetchException):
    """General fetcher exception when something happens incorrectly when unpacking"""
    def __init__(self, message, url):
        msg = "Unpack failure for URL: '%s'. %s" % (url, message)
        self.url = url
        BBFetchException.__init__(self, msg)
        self.args = (message, url)

class NoMethodError(BBFetchException):
    """Exception raised when there is no method to obtain a supplied url or set of urls"""
    def __init__(self, url):
        msg = "Could not find a fetcher which supports the URL: '%s'" % url
        self.url = url
        BBFetchException.__init__(self, msg)
        self.args = (url,)

class MissingParameterError(BBFetchException):
    """Exception raised when a fetch method is missing a critical parameter in the url"""
    def __init__(self, missing, url):
        msg = "URL: '%s' is missing the required parameter '%s'" % (url, missing)
        self.url = url
        self.missing = missing
        BBFetchException.__init__(self, msg)
        self.args = (missing, url)

class ParameterError(BBFetchException):
    """Exception raised when a url cannot be proccessed due to invalid parameters."""
    def __init__(self, message, url):
        msg = "URL: '%s' has invalid parameters. %s" % (url, message)
        self.url = url
        BBFetchException.__init__(self, msg)
        self.args = (message, url)

class NetworkAccess(BBFetchException):
    """Exception raised when network access is disabled but it is required."""
    def __init__(self, url, cmd):
        msg = "Network access disabled through BB_NO_NETWORK (or set indirectly due to use of BB_FETCH_PREMIRRORONLY) but access requested with command %s (for url %s)" % (cmd, url)
        self.url = url
        self.cmd = cmd
        BBFetchException.__init__(self, msg)
        self.args = (url, cmd)

class NonLocalMethod(Exception):
    def __init__(self):
        Exception.__init__(self)

class MissingChecksumEvent(bb.event.Event):
    def __init__(self, url, **checksums):
        self.url = url
        self.checksums = checksums
        bb.event.Event.__init__(self)


class URI(object):
    """
    A class representing a generic URI, with methods for
    accessing the URI components, and stringifies to the
    URI.

    It is constructed by calling it with a URI, or setting
    the attributes manually:

     uri = URI("http://example.com/")

     uri = URI()
     uri.scheme = 'http'
     uri.hostname = 'example.com'
     uri.path = '/'

    It has the following attributes:

      * scheme (read/write)
      * userinfo (authentication information) (read/write)
        * username (read/write)
        * password (read/write)

        Note, password is deprecated as of RFC 3986.

      * hostname (read/write)
      * port (read/write)
      * hostport (read only)
        "hostname:port", if both are set, otherwise just "hostname"
      * path (read/write)
      * path_quoted (read/write)
        A URI quoted version of path
      * params (dict) (read/write)
      * query (dict) (read/write)
      * relative (bool) (read only)
        True if this is a "relative URI", (e.g. file:foo.diff)

    It stringifies to the URI itself.

    Some notes about relative URIs: while it's specified that
    a URI beginning with <scheme>:// should either be directly
    followed by a hostname or a /, the old URI handling of the
    fetch2 library did not comform to this. Therefore, this URI
    class has some kludges to make sure that URIs are parsed in
    a way comforming to bitbake's current usage. This URI class
    supports the following:

     file:relative/path.diff (IETF compliant)
     git:relative/path.git (IETF compliant)
     git:///absolute/path.git (IETF compliant)
     file:///absolute/path.diff (IETF compliant)

     file://relative/path.diff (not IETF compliant)

    But it does not support the following:

     file://hostname/absolute/path.diff (would be IETF compliant)

    Note that the last case only applies to a list of
    "whitelisted" schemes (currently only file://), that requires
    its URIs to not have a network location.
    """

    _relative_schemes = ['file', 'git']
    _netloc_forbidden = ['file']

    def __init__(self, uri=None):
        self.scheme = ''
        self.userinfo = ''
        self.hostname = ''
        self.port = None
        self._path = ''
        self.params = {}
        self.query = {}
        self.relative = False

        if not uri:
            return

        # We hijack the URL parameters, since the way bitbake uses
        # them are not quite RFC compliant.
        uri, param_str = (uri.split(";", 1) + [None])[:2]

        urlp = urllib.parse.urlparse(uri)
        self.scheme = urlp.scheme

        reparse = 0

        # Coerce urlparse to make URI scheme use netloc
        if not self.scheme in urllib.parse.uses_netloc:
            urllib.parse.uses_params.append(self.scheme)
            reparse = 1

        # Make urlparse happy(/ier) by converting local resources
        # to RFC compliant URL format. E.g.:
        #   file://foo.diff -> file:foo.diff
        if urlp.scheme in self._netloc_forbidden:
            uri = re.sub("(?<=:)//(?!/)", "", uri, 1)
            reparse = 1

        if reparse:
            urlp = urllib.parse.urlparse(uri)

        # Identify if the URI is relative or not
        if urlp.scheme in self._relative_schemes and \
           re.compile(r"^\w+:(?!//)").match(uri):
            self.relative = True

        if not self.relative:
            self.hostname = urlp.hostname or ''
            self.port = urlp.port

            self.userinfo += urlp.username or ''

            if urlp.password:
                self.userinfo += ':%s' % urlp.password

        self.path = urllib.parse.unquote(urlp.path)

        if param_str:
            self.params = self._param_str_split(param_str, ";")
        if urlp.query:
            self.query = self._param_str_split(urlp.query, "&")

    def __str__(self):
        userinfo = self.userinfo
        if userinfo:
            userinfo += '@'

        return "%s:%s%s%s%s%s%s" % (
            self.scheme,
            '' if self.relative else '//',
            userinfo,
            self.hostport,
            self.path_quoted,
            self._query_str(),
            self._param_str())

    def _param_str(self):
        return (
            ''.join([';', self._param_str_join(self.params, ";")])
            if self.params else '')

    def _query_str(self):
        return (
            ''.join(['?', self._param_str_join(self.query, "&")])
            if self.query else '')

    def _param_str_split(self, string, elmdelim, kvdelim="="):
        ret = collections.OrderedDict()
        for k, v in [x.split(kvdelim, 1) for x in string.split(elmdelim) if x]:
            ret[k] = v
        return ret

    def _param_str_join(self, dict_, elmdelim, kvdelim="="):
        return elmdelim.join([kvdelim.join([k, v]) for k, v in dict_.items()])

    @property
    def hostport(self):
        if not self.port:
            return self.hostname
        return "%s:%d" % (self.hostname, self.port)

    @property
    def path_quoted(self):
        return urllib.parse.quote(self.path)

    @path_quoted.setter
    def path_quoted(self, path):
        self.path = urllib.parse.unquote(path)

    @property
    def path(self):
        return self._path

    @path.setter
    def path(self, path):
        self._path = path

        if not path or re.compile("^/").match(path):
            self.relative = False
        else:
            self.relative = True

    @property
    def username(self):
        if self.userinfo:
            return (self.userinfo.split(":", 1))[0]
        return ''

    @username.setter
    def username(self, username):
        password = self.password
        self.userinfo = username
        if password:
            self.userinfo += ":%s" % password

    @property
    def password(self):
        if self.userinfo and ":" in self.userinfo:
            return (self.userinfo.split(":", 1))[1]
        return ''

    @password.setter
    def password(self, password):
        self.userinfo = "%s:%s" % (self.username, password)

def decodeurl(url):
    """Decodes an URL into the tokens (scheme, network location, path,
    user, password, parameters).
    """

    m = re.compile('(?P<type>[^:]*)://((?P<user>[^/;]+)@)?(?P<location>[^;]+)(;(?P<parm>.*))?').match(url)
    if not m:
        raise MalformedUrl(url)

    type = m.group('type')
    location = m.group('location')
    if not location:
        raise MalformedUrl(url)
    user = m.group('user')
    parm = m.group('parm')

    locidx = location.find('/')
    if locidx != -1 and type.lower() != 'file':
        host = location[:locidx]
        path = location[locidx:]
    elif type.lower() == 'file':
        host = ""
        path = location
    else:
        host = location
        path = "/"
    if user:
        m = re.compile('(?P<user>[^:]+)(:?(?P<pswd>.*))').match(user)
        if m:
            user = m.group('user')
            pswd = m.group('pswd')
    else:
        user = ''
        pswd = ''

    p = collections.OrderedDict()
    if parm:
        for s in parm.split(';'):
            if s:
                if not '=' in s:
                    raise MalformedUrl(url, "The URL: '%s' is invalid: parameter %s does not specify a value (missing '=')" % (url, s))
                s1, s2 = s.split('=')
                p[s1] = s2

    return type, host, urllib.parse.unquote(path), user, pswd, p

def encodeurl(decoded):
    """Encodes a URL from tokens (scheme, network location, path,
    user, password, parameters).
    """

    type, host, path, user, pswd, p = decoded

    if not type:
        raise MissingParameterError('type', "encoded from the data %s" % str(decoded))
    url = '%s://' % type
    if user and type != "file":
        url += "%s" % user
        if pswd:
            url += ":%s" % pswd
        url += "@"
    if host and type != "file":
        url += "%s" % host
    if path:
        # Standardise path to ensure comparisons work
        while '//' in path:
            path = path.replace("//", "/")
        url += "%s" % urllib.parse.quote(path)
    if p:
        for parm in p:
            url += ";%s=%s" % (parm, p[parm])

    return url

def uri_replace(ud, uri_find, uri_replace, replacements, d, mirrortarball=None):
    if not ud.url or not uri_find or not uri_replace:
        logger.error("uri_replace: passed an undefined value, not replacing")
        return None
    uri_decoded = list(decodeurl(ud.url))
    uri_find_decoded = list(decodeurl(uri_find))
    uri_replace_decoded = list(decodeurl(uri_replace))
    logger.debug2("For url %s comparing %s to %s" % (uri_decoded, uri_find_decoded, uri_replace_decoded))
    result_decoded = ['', '', '', '', '', {}]
    for loc, i in enumerate(uri_find_decoded):
        result_decoded[loc] = uri_decoded[loc]
        regexp = i
        if loc == 0 and regexp and not regexp.endswith("$"):
            # Leaving the type unanchored can mean "https" matching "file" can become "files"
            # which is clearly undesirable.
            regexp += "$"
        if loc == 5:
            # Handle URL parameters
            if i:
                # Any specified URL parameters must match
                for k in uri_find_decoded[loc]:
                    if uri_decoded[loc][k] != uri_find_decoded[loc][k]:
                        return None
            # Overwrite any specified replacement parameters
            for k in uri_replace_decoded[loc]:
                for l in replacements:
                    uri_replace_decoded[loc][k] = uri_replace_decoded[loc][k].replace(l, replacements[l])
                result_decoded[loc][k] = uri_replace_decoded[loc][k]
        elif (re.match(regexp, uri_decoded[loc])):
            if not uri_replace_decoded[loc]:
                result_decoded[loc] = ""
            else:
                for k in replacements:
                    uri_replace_decoded[loc] = uri_replace_decoded[loc].replace(k, replacements[k])
                #bb.note("%s %s %s" % (regexp, uri_replace_decoded[loc], uri_decoded[loc]))
                result_decoded[loc] = re.sub(regexp, uri_replace_decoded[loc], uri_decoded[loc], 1)
            if loc == 2:
                # Handle path manipulations
                basename = None
                if uri_decoded[0] != uri_replace_decoded[0] and mirrortarball:
                    # If the source and destination url types differ, must be a mirrortarball mapping
                    basename = os.path.basename(mirrortarball)
                    # Kill parameters, they make no sense for mirror tarballs
                    uri_decoded[5] = {}
                elif ud.localpath and ud.method.supports_checksum(ud):
                    basename = os.path.basename(ud.localpath)
                if basename and not result_decoded[loc].endswith(basename):
                    result_decoded[loc] = os.path.join(result_decoded[loc], basename)
        else:
            return None
    result = encodeurl(result_decoded)
    if result == ud.url:
        return None
    logger.debug2("For url %s returning %s" % (ud.url, result))
    return result

methods = []
urldata_cache = {}
saved_headrevs = {}

def fetcher_init(d):
    """
    Called to initialize the fetchers once the configuration data is known.
    Calls before this must not hit the cache.
    """

    revs = bb.persist_data.persist('BB_URI_HEADREVS', d)
    try:
        # fetcher_init is called multiple times, so make sure we only save the
        # revs the first time it is called.
        if not bb.fetch2.saved_headrevs:
            bb.fetch2.saved_headrevs = dict(revs)
    except:
        pass

    # When to drop SCM head revisions controlled by user policy
    srcrev_policy = d.getVar('BB_SRCREV_POLICY') or "clear"
    if srcrev_policy == "cache":
        logger.debug("Keeping SRCREV cache due to cache policy of: %s", srcrev_policy)
    elif srcrev_policy == "clear":
        logger.debug("Clearing SRCREV cache due to cache policy of: %s", srcrev_policy)
        revs.clear()
    else:
        raise FetchError("Invalid SRCREV cache policy of: %s" % srcrev_policy)

    _checksum_cache.init_cache(d)

    for m in methods:
        if hasattr(m, "init"):
            m.init(d)

def fetcher_parse_save():
    _checksum_cache.save_extras()

def fetcher_parse_done():
    _checksum_cache.save_merge()

def fetcher_compare_revisions(d):
    """
    Compare the revisions in the persistent cache with the saved values from
    when bitbake was started and return true if they have changed.
    """

    headrevs = dict(bb.persist_data.persist('BB_URI_HEADREVS', d))
    return headrevs != bb.fetch2.saved_headrevs

def mirror_from_string(data):
    mirrors = (data or "").replace('\\n',' ').split()
    # Split into pairs
    if len(mirrors) % 2 != 0:
        bb.warn('Invalid mirror data %s, should have paired members.' % data)
    return list(zip(*[iter(mirrors)]*2))

def verify_checksum(ud, d, precomputed={}):
    """
    verify the MD5 and SHA256 checksum for downloaded src

    Raises a FetchError if one or both of the SRC_URI checksums do not match
    the downloaded file, or if BB_STRICT_CHECKSUM is set and there are no
    checksums specified.

    Returns a dict of checksums that can be stored in a done stamp file and
    passed in as precomputed parameter in a later call to avoid re-computing
    the checksums from the file. This allows verifying the checksums of the
    file against those in the recipe each time, rather than only after
    downloading. See https://bugzilla.yoctoproject.org/show_bug.cgi?id=5571.
    """

    if ud.ignore_checksums or not ud.method.supports_checksum(ud):
        return {}

    def compute_checksum_info(checksum_id):
        checksum_name = getattr(ud, "%s_name" % checksum_id)

        if checksum_id in precomputed:
            checksum_data = precomputed[checksum_id]
        else:
            checksum_data = getattr(bb.utils, "%s_file" % checksum_id)(ud.localpath)

        checksum_expected = getattr(ud, "%s_expected" % checksum_id)

        return {
            "id": checksum_id,
            "name": checksum_name,
            "data": checksum_data,
            "expected": checksum_expected
        }

    checksum_infos = []
    for checksum_id in CHECKSUM_LIST:
        checksum_infos.append(compute_checksum_info(checksum_id))

    checksum_dict = {ci["id"] : ci["data"] for ci in checksum_infos}
    checksum_event = {"%ssum" % ci["id"] : ci["data"] for ci in checksum_infos}

    for ci in checksum_infos:
        if ci["id"] in SHOWN_CHECKSUM_LIST:
            checksum_lines = ["SRC_URI[%s] = \"%s\"" % (ci["name"], ci["data"])]

    # If no checksum has been provided
    if ud.method.recommends_checksum(ud) and all(ci["expected"] is None for ci in checksum_infos):
        messages = []
        strict = d.getVar("BB_STRICT_CHECKSUM") or "0"

        # If strict checking enabled and neither sum defined, raise error
        if strict == "1":
            messages.append("No checksum specified for '%s', please add at " \
                            "least one to the recipe:" % ud.localpath)
            messages.extend(checksum_lines)
            logger.error("\n".join(messages))
            raise NoChecksumError("Missing SRC_URI checksum", ud.url)

        bb.event.fire(MissingChecksumEvent(ud.url, **checksum_event), d)

        if strict == "ignore":
            return checksum_dict

        # Log missing sums so user can more easily add them
        messages.append("Missing checksum for '%s', consider adding at " \
                        "least one to the recipe:" % ud.localpath)
        messages.extend(checksum_lines)
        logger.warning("\n".join(messages))

    # We want to alert the user if a checksum is defined in the recipe but
    # it does not match.
    messages = []
    messages.append("Checksum mismatch!")
    bad_checksum = None

    for ci in checksum_infos:
        if ci["expected"] and ci["expected"] != ci["data"]:
            messages.append("File: '%s' has %s checksum %s when %s was " \
                            "expected" % (ud.localpath, ci["id"], ci["data"], ci["expected"]))
            bad_checksum = ci["data"]

    if bad_checksum:
        messages.append("If this change is expected (e.g. you have upgraded " \
                        "to a new version without updating the checksums) " \
                        "then you can use these lines within the recipe:")
        messages.extend(checksum_lines)
        messages.append("Otherwise you should retry the download and/or " \
                        "check with upstream to determine if the file has " \
                        "become corrupted or otherwise unexpectedly modified.")
        raise ChecksumError("\n".join(messages), ud.url, bad_checksum)

    return checksum_dict

def verify_donestamp(ud, d, origud=None):
    """
    Check whether the done stamp file has the right checksums (if the fetch
    method supports them). If it doesn't, delete the done stamp and force
    a re-download.

    Returns True, if the donestamp exists and is valid, False otherwise. When
    returning False, any existing done stamps are removed.
    """
    if not ud.needdonestamp or (origud and not origud.needdonestamp):
        return True

    if not os.path.exists(ud.localpath):
        # local path does not exist
        if os.path.exists(ud.donestamp):
            # done stamp exists, but the downloaded file does not; the done stamp
            # must be incorrect, re-trigger the download
            bb.utils.remove(ud.donestamp)
        return False

    if (not ud.method.supports_checksum(ud) or
        (origud and not origud.method.supports_checksum(origud))):
        # if done stamp exists and checksums not supported; assume the local
        # file is current
        return os.path.exists(ud.donestamp)

    precomputed_checksums = {}
    # Only re-use the precomputed checksums if the donestamp is newer than the
    # file. Do not rely on the mtime of directories, though. If ud.localpath is
    # a directory, there will probably not be any checksums anyway.
    if os.path.exists(ud.donestamp) and (os.path.isdir(ud.localpath) or
            os.path.getmtime(ud.localpath) < os.path.getmtime(ud.donestamp)):
        try:
            with open(ud.donestamp, "rb") as cachefile:
                pickled = pickle.Unpickler(cachefile)
                precomputed_checksums.update(pickled.load())
        except Exception as e:
            # Avoid the warnings on the upgrade path from emtpy done stamp
            # files to those containing the checksums.
            if not isinstance(e, EOFError):
                # Ignore errors, they aren't fatal
                logger.warning("Couldn't load checksums from donestamp %s: %s "
                               "(msg: %s)" % (ud.donestamp, type(e).__name__,
                                              str(e)))

    try:
        checksums = verify_checksum(ud, d, precomputed_checksums)
        # If the cache file did not have the checksums, compute and store them
        # as an upgrade path from the previous done stamp file format.
        if checksums != precomputed_checksums:
            with open(ud.donestamp, "wb") as cachefile:
                p = pickle.Pickler(cachefile, 2)
                p.dump(checksums)
        return True
    except ChecksumError as e:
        # Checksums failed to verify, trigger re-download and remove the
        # incorrect stamp file.
        logger.warning("Checksum mismatch for local file %s\n"
                       "Cleaning and trying again." % ud.localpath)
        if os.path.exists(ud.localpath):
            rename_bad_checksum(ud, e.checksum)
        bb.utils.remove(ud.donestamp)
    return False


def update_stamp(ud, d):
    """
        donestamp is file stamp indicating the whole fetching is done
        this function update the stamp after verifying the checksum
    """
    if not ud.needdonestamp:
        return

    if os.path.exists(ud.donestamp):
        # Touch the done stamp file to show active use of the download
        try:
            os.utime(ud.donestamp, None)
        except:
            # Errors aren't fatal here
            pass
    else:
        try:
            checksums = verify_checksum(ud, d)
            # Store the checksums for later re-verification against the recipe
            with open(ud.donestamp, "wb") as cachefile:
                p = pickle.Pickler(cachefile, 2)
                p.dump(checksums)
        except ChecksumError as e:
            # Checksums failed to verify, trigger re-download and remove the
            # incorrect stamp file.
            logger.warning("Checksum mismatch for local file %s\n"
                           "Cleaning and trying again." % ud.localpath)
            if os.path.exists(ud.localpath):
                rename_bad_checksum(ud, e.checksum)
            bb.utils.remove(ud.donestamp)
            raise

def subprocess_setup():
    # Python installs a SIGPIPE handler by default. This is usually not what
    # non-Python subprocesses expect.
    # SIGPIPE errors are known issues with gzip/bash
    signal.signal(signal.SIGPIPE, signal.SIG_DFL)

def get_autorev(d):
    #  only not cache src rev in autorev case
    if d.getVar('BB_SRCREV_POLICY') != "cache":
        d.setVar('BB_DONT_CACHE', '1')
    return "AUTOINC"

def get_srcrev(d, method_name='sortable_revision'):
    """
    Return the revision string, usually for use in the version string (PV) of the current package
    Most packages usually only have one SCM so we just pass on the call.
    In the multi SCM case, we build a value based on SRCREV_FORMAT which must
    have been set.

    The idea here is that we put the string "AUTOINC+" into return value if the revisions are not
    incremental, other code is then responsible for turning that into an increasing value (if needed)

    A method_name can be supplied to retrieve an alternatively formatted revision from a fetcher, if
    that fetcher provides a method with the given name and the same signature as sortable_revision.
    """

    scms = []
    fetcher = Fetch(d.getVar('SRC_URI').split(), d)
    urldata = fetcher.ud
    for u in urldata:
        if urldata[u].method.supports_srcrev():
            scms.append(u)

    if len(scms) == 0:
        raise FetchError("SRCREV was used yet no valid SCM was found in SRC_URI")

    if len(scms) == 1 and len(urldata[scms[0]].names) == 1:
        autoinc, rev = getattr(urldata[scms[0]].method, method_name)(urldata[scms[0]], d, urldata[scms[0]].names[0])
        if len(rev) > 10:
            rev = rev[:10]
        if autoinc:
            return "AUTOINC+" + rev
        return rev

    #
    # Mutiple SCMs are in SRC_URI so we resort to SRCREV_FORMAT
    #
    format = d.getVar('SRCREV_FORMAT')
    if not format:
        raise FetchError("The SRCREV_FORMAT variable must be set when multiple SCMs are used.\n"\
                         "The SCMs are:\n%s" % '\n'.join(scms))

    name_to_rev = {}
    seenautoinc = False
    for scm in scms:
        ud = urldata[scm]
        for name in ud.names:
            autoinc, rev = getattr(ud.method, method_name)(ud, d, name)
            seenautoinc = seenautoinc or autoinc
            if len(rev) > 10:
                rev = rev[:10]
            name_to_rev[name] = rev
    # Replace names by revisions in the SRCREV_FORMAT string. The approach used
    # here can handle names being prefixes of other names and names appearing
    # as substrings in revisions (in which case the name should not be
    # expanded). The '|' regular expression operator tries matches from left to
    # right, so we need to sort the names with the longest ones first.
    names_descending_len = sorted(name_to_rev, key=len, reverse=True)
    name_to_rev_re = "|".join(re.escape(name) for name in names_descending_len)
    format = re.sub(name_to_rev_re, lambda match: name_to_rev[match.group(0)], format)

    if seenautoinc:
        format = "AUTOINC+" + format

    return format

def localpath(url, d):
    fetcher = bb.fetch2.Fetch([url], d)
    return fetcher.localpath(url)

def runfetchcmd(cmd, d, quiet=False, cleanup=None, log=None, workdir=None):
    """
    Run cmd returning the command output
    Raise an error if interrupted or cmd fails
    Optionally echo command output to stdout
    Optionally remove the files/directories listed in cleanup upon failure
    """

    # Need to export PATH as binary could be in metadata paths
    # rather than host provided
    # Also include some other variables.
    # FIXME: Should really include all export varaiables?
    exportvars = ['HOME', 'PATH',
                  'HTTP_PROXY', 'http_proxy',
                  'HTTPS_PROXY', 'https_proxy',
                  'FTP_PROXY', 'ftp_proxy',
                  'FTPS_PROXY', 'ftps_proxy',
                  'NO_PROXY', 'no_proxy',
                  'ALL_PROXY', 'all_proxy',
                  'GIT_PROXY_COMMAND',
                  'GIT_SSH',
                  'GIT_SSL_CAINFO',
                  'GIT_SMART_HTTP',
                  'SSH_AUTH_SOCK', 'SSH_AGENT_PID',
                  'SOCKS5_USER', 'SOCKS5_PASSWD',
                  'DBUS_SESSION_BUS_ADDRESS',
                  'P4CONFIG']

    if not cleanup:
        cleanup = []

    # If PATH contains WORKDIR which contains PV-PR which contains SRCPV we
    # can end up in circular recursion here so give the option of breaking it
    # in a data store copy.
    try:
        d.getVar("PV")
        d.getVar("PR")
    except bb.data_smart.ExpansionError:
        d = bb.data.createCopy(d)
        d.setVar("PV", "fetcheravoidrecurse")
        d.setVar("PR", "fetcheravoidrecurse")

    origenv = d.getVar("BB_ORIGENV", False)
    for var in exportvars:
        val = d.getVar(var) or (origenv and origenv.getVar(var))
        if val:
            cmd = 'export ' + var + '=\"%s\"; %s' % (val, cmd)

    # Disable pseudo as it may affect ssh, potentially causing it to hang.
    cmd = 'export PSEUDO_DISABLED=1; ' + cmd

    if workdir:
        logger.debug("Running '%s' in %s" % (cmd, workdir))
    else:
        logger.debug("Running %s", cmd)

    success = False
    error_message = ""

    try:
        (output, errors) = bb.process.run(cmd, log=log, shell=True, stderr=subprocess.PIPE, cwd=workdir)
        success = True
    except bb.process.NotFoundError as e:
        error_message = "Fetch command %s" % (e.command)
    except bb.process.ExecutionError as e:
        if e.stdout:
            output = "output:\n%s\n%s" % (e.stdout, e.stderr)
        elif e.stderr:
            output = "output:\n%s" % e.stderr
        else:
            output = "no output"
        error_message = "Fetch command %s failed with exit code %s, %s" % (e.command, e.exitcode, output)
    except bb.process.CmdError as e:
        error_message = "Fetch command %s could not be run:\n%s" % (e.command, e.msg)
    if not success:
        for f in cleanup:
            try:
                bb.utils.remove(f, True)
            except OSError:
                pass

        raise FetchError(error_message)

    return output

def check_network_access(d, info, url):
    """
    log remote network access, and error if BB_NO_NETWORK is set or the given
    URI is untrusted
    """
    if bb.utils.to_boolean(d.getVar("BB_NO_NETWORK")):
        raise NetworkAccess(url, info)
    elif not trusted_network(d, url):
        raise UntrustedUrl(url, info)
    else:
        logger.debug("Fetcher accessed the network with the command %s" % info)

def build_mirroruris(origud, mirrors, ld):
    uris = []
    uds = []

    replacements = {}
    replacements["TYPE"] = origud.type
    replacements["HOST"] = origud.host
    replacements["PATH"] = origud.path
    replacements["BASENAME"] = origud.path.split("/")[-1]
    replacements["MIRRORNAME"] = origud.host.replace(':','.') + origud.path.replace('/', '.').replace('*', '.')

    def adduri(ud, uris, uds, mirrors, tarballs):
        for line in mirrors:
            try:
                (find, replace) = line
            except ValueError:
                continue

            for tarball in tarballs:
                newuri = uri_replace(ud, find, replace, replacements, ld, tarball)
                if not newuri or newuri in uris or newuri == origud.url:
                    continue

                if not trusted_network(ld, newuri):
                    logger.debug("Mirror %s not in the list of trusted networks, skipping" %  (newuri))
                    continue

                # Create a local copy of the mirrors minus the current line
                # this will prevent us from recursively processing the same line
                # as well as indirect recursion A -> B -> C -> A
                localmirrors = list(mirrors)
                localmirrors.remove(line)

                try:
                    newud = FetchData(newuri, ld)
                    newud.setup_localpath(ld)
                except bb.fetch2.BBFetchException as e:
                    logger.debug("Mirror fetch failure for url %s (original url: %s)" % (newuri, origud.url))
                    logger.debug(str(e))
                    try:
                        # setup_localpath of file:// urls may fail, we should still see
                        # if mirrors of the url exist
                        adduri(newud, uris, uds, localmirrors, tarballs)
                    except UnboundLocalError:
                        pass
                    continue
                uris.append(newuri)
                uds.append(newud)

                adduri(newud, uris, uds, localmirrors, tarballs)

    adduri(origud, uris, uds, mirrors, origud.mirrortarballs or [None])

    return uris, uds

def rename_bad_checksum(ud, suffix):
    """
    Renames files to have suffix from parameter
    """

    if ud.localpath is None:
        return

    new_localpath = "%s_bad-checksum_%s" % (ud.localpath, suffix)
    bb.warn("Renaming %s to %s" % (ud.localpath, new_localpath))
    if not bb.utils.movefile(ud.localpath, new_localpath):
        bb.warn("Renaming %s to %s failed, grep movefile in log.do_fetch to see why" % (ud.localpath, new_localpath))


def try_mirror_url(fetch, origud, ud, ld, check = False):
    # Return of None or a value means we're finished
    # False means try another url

    if ud.lockfile and ud.lockfile != origud.lockfile:
        lf = bb.utils.lockfile(ud.lockfile)

    try:
        if check:
            found = ud.method.checkstatus(fetch, ud, ld)
            if found:
                return found
            return False

        if not verify_donestamp(ud, ld, origud) or ud.method.need_update(ud, ld):
            ud.method.download(ud, ld)
            if hasattr(ud.method,"build_mirror_data"):
                ud.method.build_mirror_data(ud, ld)

        if not ud.localpath or not os.path.exists(ud.localpath):
            return False

        if ud.localpath == origud.localpath:
            return ud.localpath

        # We may be obtaining a mirror tarball which needs further processing by the real fetcher
        # If that tarball is a local file:// we need to provide a symlink to it
        dldir = ld.getVar("DL_DIR")

        if origud.mirrortarballs and os.path.basename(ud.localpath) in origud.mirrortarballs and os.path.basename(ud.localpath) != os.path.basename(origud.localpath):
            # Create donestamp in old format to avoid triggering a re-download
            if ud.donestamp:
                bb.utils.mkdirhier(os.path.dirname(ud.donestamp))
                open(ud.donestamp, 'w').close()
            dest = os.path.join(dldir, os.path.basename(ud.localpath))
            if not os.path.exists(dest):
                # In case this is executing without any file locks held (as is
                # the case for file:// URLs), two tasks may end up here at the
                # same time, in which case we do not want the second task to
                # fail when the link has already been created by the first task.
                try:
                    os.symlink(ud.localpath, dest)
                except FileExistsError:
                    pass
            if not verify_donestamp(origud, ld) or origud.method.need_update(origud, ld):
                origud.method.download(origud, ld)
                if hasattr(origud.method, "build_mirror_data"):
                    origud.method.build_mirror_data(origud, ld)
            return origud.localpath
        # Otherwise the result is a local file:// and we symlink to it
        ensure_symlink(ud.localpath, origud.localpath)
        update_stamp(origud, ld)
        return ud.localpath

    except bb.fetch2.NetworkAccess:
        raise

    except IOError as e:
        if e.errno in [errno.ESTALE]:
            logger.warning("Stale Error Observed %s." % ud.url)
            return False
        raise

    except bb.fetch2.BBFetchException as e:
        if isinstance(e, ChecksumError):
            logger.warning("Mirror checksum failure for url %s (original url: %s)\nCleaning and trying again." % (ud.url, origud.url))
            logger.warning(str(e))
            if os.path.exists(ud.localpath):
                rename_bad_checksum(ud, e.checksum)
        elif isinstance(e, NoChecksumError):
            raise
        else:
            logger.debug("Mirror fetch failure for url %s (original url: %s)" % (ud.url, origud.url))
            logger.debug(str(e))
        try:
            ud.method.clean(ud, ld)
        except UnboundLocalError:
            pass
        return False
    finally:
        if ud.lockfile and ud.lockfile != origud.lockfile:
            bb.utils.unlockfile(lf)


def ensure_symlink(target, link_name):
    if not os.path.exists(link_name):
        if os.path.islink(link_name):
            # Broken symbolic link
            os.unlink(link_name)

        # In case this is executing without any file locks held (as is
        # the case for file:// URLs), two tasks may end up here at the
        # same time, in which case we do not want the second task to
        # fail when the link has already been created by the first task.
        try:
            os.symlink(target, link_name)
        except FileExistsError:
            pass


def try_mirrors(fetch, d, origud, mirrors, check = False):
    """
    Try to use a mirrored version of the sources.
    This method will be automatically called before the fetchers go.

    d Is a bb.data instance
    uri is the original uri we're trying to download
    mirrors is the list of mirrors we're going to try
    """
    ld = d.createCopy()

    uris, uds = build_mirroruris(origud, mirrors, ld)

    for index, uri in enumerate(uris):
        ret = try_mirror_url(fetch, origud, uds[index], ld, check)
        if ret:
            return ret
    return None

def trusted_network(d, url):
    """
    Use a trusted url during download if networking is enabled and
    BB_ALLOWED_NETWORKS is set globally or for a specific recipe.
    Note: modifies SRC_URI & mirrors.
    """
    if bb.utils.to_boolean(d.getVar("BB_NO_NETWORK")):
        return True

    pkgname = d.expand(d.getVar('PN', False))
    trusted_hosts = None
    if pkgname:
        trusted_hosts = d.getVarFlag('BB_ALLOWED_NETWORKS', pkgname, False)

    if not trusted_hosts:
        trusted_hosts = d.getVar('BB_ALLOWED_NETWORKS')

    # Not enabled.
    if not trusted_hosts:
        return True

    scheme, network, path, user, passwd, param = decodeurl(url)

    if not network:
        return True

    network = network.split(':')[0]
    network = network.lower()

    for host in trusted_hosts.split(" "):
        host = host.lower()
        if host.startswith("*.") and ("." + network).endswith(host[1:]):
            return True
        if host == network:
            return True

    return False

def srcrev_internal_helper(ud, d, name):
    """
    Return:
        a) a source revision if specified
        b) latest revision if SRCREV="AUTOINC"
        c) None if not specified
    """

    srcrev = None
    pn = d.getVar("PN")
    attempts = []
    if name != '' and pn:
        attempts.append("SRCREV_%s_pn-%s" % (name, pn))
    if name != '':
        attempts.append("SRCREV_%s" % name)
    if pn:
        attempts.append("SRCREV_pn-%s" % pn)
    attempts.append("SRCREV")

    for a in attempts:
        srcrev = d.getVar(a)
        if srcrev and srcrev != "INVALID":
            break

    if 'rev' in ud.parm and 'tag' in ud.parm:
        raise FetchError("Please specify a ;rev= parameter or a ;tag= parameter in the url %s but not both." % (ud.url))

    if 'rev' in ud.parm or 'tag' in ud.parm:
        if 'rev' in ud.parm:
            parmrev = ud.parm['rev']
        else:
            parmrev = ud.parm['tag']
        if srcrev == "INVALID" or not srcrev:
            return parmrev
        if srcrev != parmrev:
            raise FetchError("Conflicting revisions (%s from SRCREV and %s from the url) found, please specify one valid value" % (srcrev, parmrev))
        return parmrev

    if srcrev == "INVALID" or not srcrev:
        raise FetchError("Please set a valid SRCREV for url %s (possible key names are %s, or use a ;rev=X URL parameter)" % (str(attempts), ud.url), ud.url)
    if srcrev == "AUTOINC":
        srcrev = ud.method.latest_revision(ud, d, name)

    return srcrev

def get_checksum_file_list(d):
    """ Get a list of files checksum in SRC_URI

    Returns the resolved local paths of all local file entries in
    SRC_URI as a space-separated string
    """
    fetch = Fetch([], d, cache = False, localonly = True)

    dl_dir = d.getVar('DL_DIR')
    filelist = []
    for u in fetch.urls:
        ud = fetch.ud[u]

        if ud and isinstance(ud.method, local.Local):
            paths = ud.method.localpaths(ud, d)
            for f in paths:
                pth = ud.decodedurl
                if f.startswith(dl_dir):
                    # The local fetcher's behaviour is to return a path under DL_DIR if it couldn't find the file anywhere else
                    if os.path.exists(f):
                        bb.warn("Getting checksum for %s SRC_URI entry %s: file not found except in DL_DIR" % (d.getVar('PN'), os.path.basename(f)))
                    else:
                        bb.warn("Unable to get checksum for %s SRC_URI entry %s: file could not be found" % (d.getVar('PN'), os.path.basename(f)))
                filelist.append(f + ":" + str(os.path.exists(f)))

    return " ".join(filelist)

def get_file_checksums(filelist, pn, localdirsexclude):
    """Get a list of the checksums for a list of local files

    Returns the checksums for a list of local files, caching the results as
    it proceeds

    """
    return _checksum_cache.get_checksums(filelist, pn, localdirsexclude)


class FetchData(object):
    """
    A class which represents the fetcher state for a given URI.
    """
    def __init__(self, url, d, localonly = False):
        # localpath is the location of a downloaded result. If not set, the file is local.
        self.donestamp = None
        self.needdonestamp = True
        self.localfile = ""
        self.localpath = None
        self.lockfile = None
        self.mirrortarballs = []
        self.basename = None
        self.basepath = None
        (self.type, self.host, self.path, self.user, self.pswd, self.parm) = decodeurl(d.expand(url))
        self.date = self.getSRCDate(d)
        self.url = url
        if not self.user and "user" in self.parm:
            self.user = self.parm["user"]
        if not self.pswd and "pswd" in self.parm:
            self.pswd = self.parm["pswd"]
        self.setup = False

        def configure_checksum(checksum_id):
            if "name" in self.parm:
                checksum_name = "%s.%ssum" % (self.parm["name"], checksum_id)
            else:
                checksum_name = "%ssum" % checksum_id

            setattr(self, "%s_name" % checksum_id, checksum_name)

            if checksum_name in self.parm:
                checksum_expected = self.parm[checksum_name]
            elif self.type not in ["http", "https", "ftp", "ftps", "sftp", "s3"]:
                checksum_expected = None
            else:
                checksum_expected = d.getVarFlag("SRC_URI", checksum_name)

            setattr(self, "%s_expected" % checksum_id, checksum_expected)

        for checksum_id in CHECKSUM_LIST:
            configure_checksum(checksum_id)

        self.ignore_checksums = False

        self.names = self.parm.get("name",'default').split(',')

        self.method = None
        for m in methods:
            if m.supports(self, d):
                self.method = m
                break

        if not self.method:
            raise NoMethodError(url)

        if localonly and not isinstance(self.method, local.Local):
            raise NonLocalMethod()

        if self.parm.get("proto", None) and "protocol" not in self.parm:
            logger.warning('Consider updating %s recipe to use "protocol" not "proto" in SRC_URI.', d.getVar('PN'))
            self.parm["protocol"] = self.parm.get("proto", None)

        if hasattr(self.method, "urldata_init"):
            self.method.urldata_init(self, d)

        if "localpath" in self.parm:
            # if user sets localpath for file, use it instead.
            self.localpath = self.parm["localpath"]
            self.basename = os.path.basename(self.localpath)
        elif self.localfile:
            self.localpath = self.method.localpath(self, d)

        dldir = d.getVar("DL_DIR")

        if not self.needdonestamp:
            return

        # Note: .done and .lock files should always be in DL_DIR whereas localpath may not be.
        if self.localpath and self.localpath.startswith(dldir):
            basepath = self.localpath
        elif self.localpath:
            basepath = dldir + os.sep + os.path.basename(self.localpath)
        elif self.basepath or self.basename:
            basepath = dldir + os.sep + (self.basepath or self.basename)
        else:
            bb.fatal("Can't determine lock path for url %s" % url)

        self.donestamp = basepath + '.done'
        self.lockfile = basepath + '.lock'

    def setup_revisions(self, d):
        self.revisions = {}
        for name in self.names:
            self.revisions[name] = srcrev_internal_helper(self, d, name)

        # add compatibility code for non name specified case
        if len(self.names) == 1:
            self.revision = self.revisions[self.names[0]]

    def setup_localpath(self, d):
        if not self.localpath:
            self.localpath = self.method.localpath(self, d)

    def getSRCDate(self, d):
        """
        Return the SRC Date for the component

        d the bb.data module
        """
        if "srcdate" in self.parm:
            return self.parm['srcdate']

        pn = d.getVar("PN")

        if pn:
            return d.getVar("SRCDATE_%s" % pn) or d.getVar("SRCDATE") or d.getVar("DATE")

        return d.getVar("SRCDATE") or d.getVar("DATE")

class FetchMethod(object):
    """Base class for 'fetch'ing data"""

    def __init__(self, urls=None):
        self.urls = []

    def supports(self, urldata, d):
        """
        Check to see if this fetch class supports a given url.
        """
        return 0

    def localpath(self, urldata, d):
        """
        Return the local filename of a given url assuming a successful fetch.
        Can also setup variables in urldata for use in go (saving code duplication
        and duplicate code execution)
        """
        return os.path.join(d.getVar("DL_DIR"), urldata.localfile)

    def supports_checksum(self, urldata):
        """
        Is localpath something that can be represented by a checksum?
        """

        # We cannot compute checksums for directories
        if os.path.isdir(urldata.localpath):
            return False
        return True

    def recommends_checksum(self, urldata):
        """
        Is the backend on where checksumming is recommended (should warnings
        be displayed if there is no checksum)?
        """
        return False

    def verify_donestamp(self, ud, d):
        """
        Verify the donestamp file
        """
        return verify_donestamp(ud, d)

    def update_donestamp(self, ud, d):
        """
        Update the donestamp file
        """
        update_stamp(ud, d)

    def _strip_leading_slashes(self, relpath):
        """
        Remove leading slash as os.path.join can't cope
        """
        while os.path.isabs(relpath):
            relpath = relpath[1:]
        return relpath

    def setUrls(self, urls):
        self.__urls = urls

    def getUrls(self):
        return self.__urls

    urls = property(getUrls, setUrls, None, "Urls property")

    def need_update(self, ud, d):
        """
        Force a fetch, even if localpath exists?
        """
        if os.path.exists(ud.localpath):
            return False
        return True

    def supports_srcrev(self):
        """
        The fetcher supports auto source revisions (SRCREV)
        """
        return False

    def download(self, urldata, d):
        """
        Fetch urls
        Assumes localpath was called first
        """
        raise NoMethodError(urldata.url)

    def unpack(self, urldata, rootdir, data):
        iterate = False
        file = urldata.localpath

        try:
            unpack = bb.utils.to_boolean(urldata.parm.get('unpack'), True)
        except ValueError as exc:
            bb.fatal("Invalid value for 'unpack' parameter for %s: %s" %
                     (file, urldata.parm.get('unpack')))

        base, ext = os.path.splitext(file)
        if ext in ['.gz', '.bz2', '.Z', '.xz', '.lz']:
            efile = os.path.join(rootdir, os.path.basename(base))
        else:
            efile = file
        cmd = None

        if unpack:
            if file.endswith('.tar'):
                cmd = 'tar x --no-same-owner -f %s' % file
            elif file.endswith('.tgz') or file.endswith('.tar.gz') or file.endswith('.tar.Z'):
                cmd = 'tar xz --no-same-owner -f %s' % file
            elif file.endswith('.tbz') or file.endswith('.tbz2') or file.endswith('.tar.bz2'):
                cmd = 'bzip2 -dc %s | tar x --no-same-owner -f -' % file
            elif file.endswith('.gz') or file.endswith('.Z') or file.endswith('.z'):
                cmd = 'gzip -dc %s > %s' % (file, efile)
            elif file.endswith('.bz2'):
                cmd = 'bzip2 -dc %s > %s' % (file, efile)
            elif file.endswith('.txz') or file.endswith('.tar.xz'):
                cmd = 'xz -dc %s | tar x --no-same-owner -f -' % file
            elif file.endswith('.xz'):
                cmd = 'xz -dc %s > %s' % (file, efile)
            elif file.endswith('.tar.lz'):
                cmd = 'lzip -dc %s | tar x --no-same-owner -f -' % file
            elif file.endswith('.lz'):
                cmd = 'lzip -dc %s > %s' % (file, efile)
            elif file.endswith('.tar.7z'):
                cmd = '7z x -so %s | tar x --no-same-owner -f -' % file
            elif file.endswith('.7z'):
                cmd = '7za x -y %s 1>/dev/null' % file
            elif file.endswith('.tzst') or file.endswith('.tar.zst'):
                cmd = 'zstd --decompress --stdout %s | tar x --no-same-owner -f -' % file
            elif file.endswith('.zst'):
                cmd = 'zstd --decompress --stdout %s > %s' % (file, efile)
            elif file.endswith('.zip') or file.endswith('.jar'):
                try:
                    dos = bb.utils.to_boolean(urldata.parm.get('dos'), False)
                except ValueError as exc:
                    bb.fatal("Invalid value for 'dos' parameter for %s: %s" %
                             (file, urldata.parm.get('dos')))
                cmd = 'unzip -q -o'
                if dos:
                    cmd = '%s -a' % cmd
                cmd = "%s '%s'" % (cmd, file)
            elif file.endswith('.rpm') or file.endswith('.srpm'):
                if 'extract' in urldata.parm:
                    unpack_file = urldata.parm.get('extract')
                    cmd = 'rpm2cpio.sh %s | cpio -id %s' % (file, unpack_file)
                    iterate = True
                    iterate_file = unpack_file
                else:
                    cmd = 'rpm2cpio.sh %s | cpio -id' % (file)
            elif file.endswith('.deb') or file.endswith('.ipk'):
                output = subprocess.check_output(['ar', '-t', file], preexec_fn=subprocess_setup)
                datafile = None
                if output:
                    for line in output.decode().splitlines():
                        if line.startswith('data.tar.'):
                            datafile = line
                            break
                    else:
                        raise UnpackError("Unable to unpack deb/ipk package - does not contain data.tar.* file", urldata.url)
                else:
                    raise UnpackError("Unable to unpack deb/ipk package - could not list contents", urldata.url)
                cmd = 'ar x %s %s && tar --no-same-owner -xpf %s && rm %s' % (file, datafile, datafile, datafile)

        # If 'subdir' param exists, create a dir and use it as destination for unpack cmd
        if 'subdir' in urldata.parm:
            subdir = urldata.parm.get('subdir')
            if os.path.isabs(subdir):
                if not os.path.realpath(subdir).startswith(os.path.realpath(rootdir)):
                    raise UnpackError("subdir argument isn't a subdirectory of unpack root %s" % rootdir, urldata.url)
                unpackdir = subdir
            else:
                unpackdir = os.path.join(rootdir, subdir)
            bb.utils.mkdirhier(unpackdir)
        else:
            unpackdir = rootdir

        if not unpack or not cmd:
            # If file == dest, then avoid any copies, as we already put the file into dest!
            dest = os.path.join(unpackdir, os.path.basename(file))
            if file != dest and not (os.path.exists(dest) and os.path.samefile(file, dest)):
                destdir = '.'
                # For file:// entries all intermediate dirs in path must be created at destination
                if urldata.type == "file":
                    # Trailing '/' does a copying to wrong place
                    urlpath = urldata.path.rstrip('/')
                    # Want files places relative to cwd so no leading '/'
                    urlpath = urlpath.lstrip('/')
                    if urlpath.find("/") != -1:
                        destdir = urlpath.rsplit("/", 1)[0] + '/'
                        bb.utils.mkdirhier("%s/%s" % (unpackdir, destdir))
                cmd = 'cp -fpPRH "%s" "%s"' % (file, destdir)

        if not cmd:
            return

        path = data.getVar('PATH')
        if path:
            cmd = "PATH=\"%s\" %s" % (path, cmd)
        bb.note("Unpacking %s to %s/" % (file, unpackdir))
        ret = subprocess.call(cmd, preexec_fn=subprocess_setup, shell=True, cwd=unpackdir)

        if ret != 0:
            raise UnpackError("Unpack command %s failed with return value %s" % (cmd, ret), urldata.url)

        if iterate is True:
            iterate_urldata = urldata
            iterate_urldata.localpath = "%s/%s" % (rootdir, iterate_file)
            self.unpack(urldata, rootdir, data)

        return

    def clean(self, urldata, d):
        """
        Clean any existing full or partial download
        """
        bb.utils.remove(urldata.localpath)

    def try_premirror(self, urldata, d):
        """
        Should premirrors be used?
        """
        return True

    def try_mirrors(self, fetch, urldata, d, mirrors, check=False):
        """
        Try to use a mirror
        """
        return bool(try_mirrors(fetch, d, urldata, mirrors, check))

    def checkstatus(self, fetch, urldata, d):
        """
        Check the status of a URL
        Assumes localpath was called first
        """
        logger.info("URL %s could not be checked for status since no method exists.", urldata.url)
        return True

    def latest_revision(self, ud, d, name):
        """
        Look in the cache for the latest revision, if not present ask the SCM.
        """
        if not hasattr(self, "_latest_revision"):
            raise ParameterError("The fetcher for this URL does not support _latest_revision", ud.url)

        revs = bb.persist_data.persist('BB_URI_HEADREVS', d)
        key = self.generate_revision_key(ud, d, name)
        try:
            return revs[key]
        except KeyError:
            revs[key] = rev = self._latest_revision(ud, d, name)
            return rev

    def sortable_revision(self, ud, d, name):
        latest_rev = self._build_revision(ud, d, name)
        return True, str(latest_rev)

    def generate_revision_key(self, ud, d, name):
        return self._revision_key(ud, d, name)

    def latest_versionstring(self, ud, d):
        """
        Compute the latest release name like "x.y.x" in "x.y.x+gitHASH"
        by searching through the tags output of ls-remote, comparing
        versions and returning the highest match as a (version, revision) pair.
        """
        return ('', '')

    def done(self, ud, d):
        """
        Is the download done ?
        """
        if os.path.exists(ud.localpath):
            return True
        return False

    def implicit_urldata(self, ud, d):
        """
        Get a list of FetchData objects for any implicit URLs that will also
        be downloaded when we fetch the given URL.
        """
        return []

class Fetch(object):
    def __init__(self, urls, d, cache = True, localonly = False, connection_cache = None):
        if localonly and cache:
            raise Exception("bb.fetch2.Fetch.__init__: cannot set cache and localonly at same time")

        if len(urls) == 0:
            urls = d.getVar("SRC_URI").split()
        self.urls = urls
        self.d = d
        self.ud = {}
        self.connection_cache = connection_cache

        fn = d.getVar('FILE')
        mc = d.getVar('__BBMULTICONFIG') or ""
        key = None
        if cache and fn:
            key = mc + fn + str(id(d))
        if key in urldata_cache:
            self.ud = urldata_cache[key]

        for url in urls:
            if url not in self.ud:
                try:
                    self.ud[url] = FetchData(url, d, localonly)
                except NonLocalMethod:
                    if localonly:
                        self.ud[url] = None
                        pass

        if key:
            urldata_cache[key] = self.ud

    def localpath(self, url):
        if url not in self.urls:
            self.ud[url] = FetchData(url, self.d)

        self.ud[url].setup_localpath(self.d)
        return self.d.expand(self.ud[url].localpath)

    def localpaths(self):
        """
        Return a list of the local filenames, assuming successful fetch
        """
        local = []

        for u in self.urls:
            ud = self.ud[u]
            ud.setup_localpath(self.d)
            local.append(ud.localpath)

        return local

    def download(self, urls=None):
        """
        Fetch all urls
        """
        if not urls:
            urls = self.urls

        network = self.d.getVar("BB_NO_NETWORK")
        premirroronly = bb.utils.to_boolean(self.d.getVar("BB_FETCH_PREMIRRORONLY"))

        for u in urls:
            ud = self.ud[u]
            ud.setup_localpath(self.d)
            m = ud.method
            done = False

            if ud.lockfile:
                lf = bb.utils.lockfile(ud.lockfile)

            try:
                self.d.setVar("BB_NO_NETWORK", network)

                if m.verify_donestamp(ud, self.d) and not m.need_update(ud, self.d):
                    done = True
                elif m.try_premirror(ud, self.d):
                    logger.debug("Trying PREMIRRORS")
                    mirrors = mirror_from_string(self.d.getVar('PREMIRRORS'))
                    done = m.try_mirrors(self, ud, self.d, mirrors)
                    if done:
                        try:
                            # early checksum verification so that if the checksum of the premirror
                            # contents mismatch the fetcher can still try upstream and mirrors
                            m.update_donestamp(ud, self.d)
                        except ChecksumError as e:
                            logger.warning("Checksum failure encountered with premirror download of %s - will attempt other sources." % u)
                            logger.debug(str(e))
                            done = False

                if premirroronly:
                    self.d.setVar("BB_NO_NETWORK", "1")

                firsterr = None
                verified_stamp = m.verify_donestamp(ud, self.d)
                if not done and (not verified_stamp or m.need_update(ud, self.d)):
                    try:
                        if not trusted_network(self.d, ud.url):
                            raise UntrustedUrl(ud.url)
                        logger.debug("Trying Upstream")
                        m.download(ud, self.d)
                        if hasattr(m, "build_mirror_data"):
                            m.build_mirror_data(ud, self.d)
                        done = True
                        # early checksum verify, so that if checksum mismatched,
                        # fetcher still have chance to fetch from mirror
                        m.update_donestamp(ud, self.d)

                    except bb.fetch2.NetworkAccess:
                        raise

                    except BBFetchException as e:
                        if isinstance(e, ChecksumError):
                            logger.warning("Checksum failure encountered with download of %s - will attempt other sources if available" % u)
                            logger.debug(str(e))
                            if os.path.exists(ud.localpath):
                                rename_bad_checksum(ud, e.checksum)
                        elif isinstance(e, NoChecksumError):
                            raise
                        else:
                            logger.warning('Failed to fetch URL %s, attempting MIRRORS if available' % u)
                            logger.debug(str(e))
                        firsterr = e
                        # Remove any incomplete fetch
                        if not verified_stamp:
                            m.clean(ud, self.d)
                        logger.debug("Trying MIRRORS")
                        mirrors = mirror_from_string(self.d.getVar('MIRRORS'))
                        done = m.try_mirrors(self, ud, self.d, mirrors)

                if not done or not m.done(ud, self.d):
                    if firsterr:
                        logger.error(str(firsterr))
                    raise FetchError("Unable to fetch URL from any source.", u)

                m.update_donestamp(ud, self.d)

            except IOError as e:
                if e.errno in [errno.ESTALE]:
                    logger.error("Stale Error Observed %s." % u)
                    raise ChecksumError("Stale Error Detected")

            except BBFetchException as e:
                if isinstance(e, ChecksumError):
                    logger.error("Checksum failure fetching %s" % u)
                raise

            finally:
                if ud.lockfile:
                    bb.utils.unlockfile(lf)

    def checkstatus(self, urls=None):
        """
        Check all urls exist upstream
        """

        if not urls:
            urls = self.urls

        for u in urls:
            ud = self.ud[u]
            ud.setup_localpath(self.d)
            m = ud.method
            logger.debug("Testing URL %s", u)
            # First try checking uri, u, from PREMIRRORS
            mirrors = mirror_from_string(self.d.getVar('PREMIRRORS'))
            ret = m.try_mirrors(self, ud, self.d, mirrors, True)
            if not ret:
                # Next try checking from the original uri, u
                ret = m.checkstatus(self, ud, self.d)
                if not ret:
                    # Finally, try checking uri, u, from MIRRORS
                    mirrors = mirror_from_string(self.d.getVar('MIRRORS'))
                    ret = m.try_mirrors(self, ud, self.d, mirrors, True)

            if not ret:
                raise FetchError("URL %s doesn't work" % u, u)

    def unpack(self, root, urls=None):
        """
        Unpack urls to root
        """

        if not urls:
            urls = self.urls

        for u in urls:
            ud = self.ud[u]
            ud.setup_localpath(self.d)

            if ud.lockfile:
                lf = bb.utils.lockfile(ud.lockfile)

            ud.method.unpack(ud, root, self.d)

            if ud.lockfile:
                bb.utils.unlockfile(lf)

    def clean(self, urls=None):
        """
        Clean files that the fetcher gets or places
        """

        if not urls:
            urls = self.urls

        for url in urls:
            if url not in self.ud:
                self.ud[url] = FetchData(url, self.d)
            ud = self.ud[url]
            ud.setup_localpath(self.d)

            if not ud.localfile and ud.localpath is None:
                continue

            if ud.lockfile:
                lf = bb.utils.lockfile(ud.lockfile)

            ud.method.clean(ud, self.d)
            if ud.donestamp:
                bb.utils.remove(ud.donestamp)

            if ud.lockfile:
                bb.utils.unlockfile(lf)

    def expanded_urldata(self, urls=None):
        """
        Get an expanded list of FetchData objects covering both the given
        URLS and any additional implicit URLs that are added automatically by
        the appropriate FetchMethod.
        """

        if not urls:
            urls = self.urls

        urldata = []
        for url in urls:
            ud = self.ud[url]
            urldata.append(ud)
            urldata += ud.method.implicit_urldata(ud, self.d)

        return urldata

class FetchConnectionCache(object):
    """
        A class which represents an container for socket connections.
    """
    def __init__(self):
        self.cache = {}

    def get_connection_name(self, host, port):
        return host + ':' + str(port)

    def add_connection(self, host, port, connection):
        cn = self.get_connection_name(host, port)

        if cn not in self.cache:
            self.cache[cn] = connection

    def get_connection(self, host, port):
        connection = None

        cn = self.get_connection_name(host, port)
        if cn in self.cache:
            connection = self.cache[cn]

        return connection

    def remove_connection(self, host, port):
        cn = self.get_connection_name(host, port)
        if cn in self.cache:
            self.cache[cn].close()
            del self.cache[cn]

    def close_connections(self):
        for cn in list(self.cache.keys()):
            self.cache[cn].close()
            del self.cache[cn]

from . import cvs
from . import git
from . import gitsm
from . import gitannex
from . import local
from . import svn
from . import wget
from . import ssh
from . import sftp
from . import s3
from . import perforce
from . import bzr
from . import hg
from . import osc
from . import repo
from . import clearcase
from . import npm
from . import npmsw

methods.append(local.Local())
methods.append(wget.Wget())
methods.append(svn.Svn())
methods.append(git.Git())
methods.append(gitsm.GitSM())
methods.append(gitannex.GitANNEX())
methods.append(cvs.Cvs())
methods.append(ssh.SSH())
methods.append(sftp.SFTP())
methods.append(s3.S3())
methods.append(perforce.Perforce())
methods.append(bzr.Bzr())
methods.append(hg.Hg())
methods.append(osc.Osc())
methods.append(repo.Repo())
methods.append(clearcase.ClearCase())
methods.append(npm.Npm())
methods.append(npmsw.NpmShrinkWrap())
