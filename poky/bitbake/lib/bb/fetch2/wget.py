"""
BitBake 'Fetch' implementations

Classes for obtaining upstream sources for the
BitBake build tools.

"""

# Copyright (C) 2003, 2004  Chris Larson
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import shlex
import re
import tempfile
import os
import errno
import bb
import bb.progress
import socket
import http.client
import urllib.request, urllib.parse, urllib.error
from   bb.fetch2 import FetchMethod
from   bb.fetch2 import FetchError
from   bb.fetch2 import logger
from   bb.fetch2 import runfetchcmd
from   bs4 import BeautifulSoup
from   bs4 import SoupStrainer

class WgetProgressHandler(bb.progress.LineFilterProgressHandler):
    """
    Extract progress information from wget output.
    Note: relies on --progress=dot (with -v or without -q/-nv) being
    specified on the wget command line.
    """
    def __init__(self, d):
        super(WgetProgressHandler, self).__init__(d)
        # Send an initial progress event so the bar gets shown
        self._fire_progress(0)

    def writeline(self, line):
        percs = re.findall(r'(\d+)%\s+([\d.]+[A-Z])', line)
        if percs:
            progress = int(percs[-1][0])
            rate = percs[-1][1] + '/s'
            self.update(progress, rate)
            return False
        return True


class Wget(FetchMethod):
    """Class to fetch urls via 'wget'"""

    # CDNs like CloudFlare may do a 'browser integrity test' which can fail
    # with the standard wget/urllib User-Agent, so pretend to be a modern
    # browser.
    user_agent = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:84.0) Gecko/20100101 Firefox/84.0"

    def check_certs(self, d):
        """
        Should certificates be checked?
        """
        return (d.getVar("BB_CHECK_SSL_CERTS") or "1") != "0"

    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with wget.
        """
        return ud.type in ['http', 'https', 'ftp', 'ftps']

    def recommends_checksum(self, urldata):
        return True

    def urldata_init(self, ud, d):
        if 'protocol' in ud.parm:
            if ud.parm['protocol'] == 'git':
                raise bb.fetch2.ParameterError("Invalid protocol - if you wish to fetch from a git repository using http, you need to instead use the git:// prefix with protocol=http", ud.url)

        if 'downloadfilename' in ud.parm:
            ud.basename = ud.parm['downloadfilename']
        else:
            ud.basename = os.path.basename(ud.path)

        ud.localfile = d.expand(urllib.parse.unquote(ud.basename))
        if not ud.localfile:
            ud.localfile = d.expand(urllib.parse.unquote(ud.host + ud.path).replace("/", "."))

        self.basecmd = d.getVar("FETCHCMD_wget") or "/usr/bin/env wget -t 2 -T 30 --passive-ftp"

        if not self.check_certs(d):
            self.basecmd += " --no-check-certificate"

    def _runwget(self, ud, d, command, quiet, workdir=None):

        progresshandler = WgetProgressHandler(d)

        logger.debug2("Fetching %s using command '%s'" % (ud.url, command))
        bb.fetch2.check_network_access(d, command, ud.url)
        runfetchcmd(command + ' --progress=dot -v', d, quiet, log=progresshandler, workdir=workdir)

    def download(self, ud, d):
        """Fetch urls"""

        fetchcmd = self.basecmd

        localpath = os.path.join(d.getVar("DL_DIR"), ud.localfile) + ".tmp"
        bb.utils.mkdirhier(os.path.dirname(localpath))
        fetchcmd += " -O %s" % shlex.quote(localpath)

        if ud.user and ud.pswd:
            fetchcmd += " --auth-no-challenge"
            if ud.parm.get("redirectauth", "1") == "1":
                # An undocumented feature of wget is that if the
                # username/password are specified on the URI, wget will only
                # send the Authorization header to the first host and not to
                # any hosts that it is redirected to.  With the increasing
                # usage of temporary AWS URLs, this difference now matters as
                # AWS will reject any request that has authentication both in
                # the query parameters (from the redirect) and in the
                # Authorization header.
                fetchcmd += " --user=%s --password=%s" % (ud.user, ud.pswd)

        uri = ud.url.split(";")[0]
        if os.path.exists(ud.localpath):
            # file exists, but we didnt complete it.. trying again..
            fetchcmd += d.expand(" -c -P ${DL_DIR} '%s'" % uri)
        else:
            fetchcmd += d.expand(" -P ${DL_DIR} '%s'" % uri)

        self._runwget(ud, d, fetchcmd, False)

        # Try and verify any checksum now, meaning if it isn't correct, we don't remove the
        # original file, which might be a race (imagine two recipes referencing the same
        # source, one with an incorrect checksum)
        bb.fetch2.verify_checksum(ud, d, localpath=localpath, fatal_nochecksum=False)

        # Remove the ".tmp" and move the file into position atomically
        # Our lock prevents multiple writers but mirroring code may grab incomplete files
        os.rename(localpath, localpath[:-4])

        # Sanity check since wget can pretend it succeed when it didn't
        # Also, this used to happen if sourceforge sent us to the mirror page
        if not os.path.exists(ud.localpath):
            raise FetchError("The fetch command returned success for url %s but %s doesn't exist?!" % (uri, ud.localpath), uri)

        if os.path.getsize(ud.localpath) == 0:
            os.remove(ud.localpath)
            raise FetchError("The fetch of %s resulted in a zero size file?! Deleting and failing since this isn't right." % (uri), uri)

        return True

    def checkstatus(self, fetch, ud, d, try_again=True):
        class HTTPConnectionCache(http.client.HTTPConnection):
            if fetch.connection_cache:
                def connect(self):
                    """Connect to the host and port specified in __init__."""

                    sock = fetch.connection_cache.get_connection(self.host, self.port)
                    if sock:
                        self.sock = sock
                    else:
                        self.sock = socket.create_connection((self.host, self.port),
                                    self.timeout, self.source_address)
                        fetch.connection_cache.add_connection(self.host, self.port, self.sock)

                    if self._tunnel_host:
                        self._tunnel()

        class CacheHTTPHandler(urllib.request.HTTPHandler):
            def http_open(self, req):
                return self.do_open(HTTPConnectionCache, req)

            def do_open(self, http_class, req):
                """Return an addinfourl object for the request, using http_class.

                http_class must implement the HTTPConnection API from httplib.
                The addinfourl return value is a file-like object.  It also
                has methods and attributes including:
                    - info(): return a mimetools.Message object for the headers
                    - geturl(): return the original request URL
                    - code: HTTP status code
                """
                host = req.host
                if not host:
                    raise urllib.error.URLError('no host given')

                h = http_class(host, timeout=req.timeout) # will parse host:port
                h.set_debuglevel(self._debuglevel)

                headers = dict(req.unredirected_hdrs)
                headers.update(dict((k, v) for k, v in list(req.headers.items())
                            if k not in headers))

                # We want to make an HTTP/1.1 request, but the addinfourl
                # class isn't prepared to deal with a persistent connection.
                # It will try to read all remaining data from the socket,
                # which will block while the server waits for the next request.
                # So make sure the connection gets closed after the (only)
                # request.

                # Don't close connection when connection_cache is enabled,
                if fetch.connection_cache is None:
                    headers["Connection"] = "close"
                else:
                    headers["Connection"] = "Keep-Alive" # Works for HTTP/1.0

                headers = dict(
                    (name.title(), val) for name, val in list(headers.items()))

                if req._tunnel_host:
                    tunnel_headers = {}
                    proxy_auth_hdr = "Proxy-Authorization"
                    if proxy_auth_hdr in headers:
                        tunnel_headers[proxy_auth_hdr] = headers[proxy_auth_hdr]
                        # Proxy-Authorization should not be sent to origin
                        # server.
                        del headers[proxy_auth_hdr]
                    h.set_tunnel(req._tunnel_host, headers=tunnel_headers)

                try:
                    h.request(req.get_method(), req.selector, req.data, headers)
                except socket.error as err: # XXX what error?
                    # Don't close connection when cache is enabled.
                    # Instead, try to detect connections that are no longer
                    # usable (for example, closed unexpectedly) and remove
                    # them from the cache.
                    if fetch.connection_cache is None:
                        h.close()
                    elif isinstance(err, OSError) and err.errno == errno.EBADF:
                        # This happens when the server closes the connection despite the Keep-Alive.
                        # Apparently urllib then uses the file descriptor, expecting it to be
                        # connected, when in reality the connection is already gone.
                        # We let the request fail and expect it to be
                        # tried once more ("try_again" in check_status()),
                        # with the dead connection removed from the cache.
                        # If it still fails, we give up, which can happen for bad
                        # HTTP proxy settings.
                        fetch.connection_cache.remove_connection(h.host, h.port)
                    raise urllib.error.URLError(err)
                else:
                    r = h.getresponse()

                # Pick apart the HTTPResponse object to get the addinfourl
                # object initialized properly.

                # Wrap the HTTPResponse object in socket's file object adapter
                # for Windows.  That adapter calls recv(), so delegate recv()
                # to read().  This weird wrapping allows the returned object to
                # have readline() and readlines() methods.

                # XXX It might be better to extract the read buffering code
                # out of socket._fileobject() and into a base class.
                r.recv = r.read

                # no data, just have to read
                r.read()
                class fp_dummy(object):
                    def read(self):
                        return ""
                    def readline(self):
                        return ""
                    def close(self):
                        pass
                    closed = False

                resp = urllib.response.addinfourl(fp_dummy(), r.msg, req.get_full_url())
                resp.code = r.status
                resp.msg = r.reason

                # Close connection when server request it.
                if fetch.connection_cache is not None:
                    if 'Connection' in r.msg and r.msg['Connection'] == 'close':
                        fetch.connection_cache.remove_connection(h.host, h.port)

                return resp

        class HTTPMethodFallback(urllib.request.BaseHandler):
            """
            Fallback to GET if HEAD is not allowed (405 HTTP error)
            """
            def http_error_405(self, req, fp, code, msg, headers):
                fp.read()
                fp.close()

                if req.get_method() != 'GET':
                    newheaders = dict((k, v) for k, v in list(req.headers.items())
                                      if k.lower() not in ("content-length", "content-type"))
                    return self.parent.open(urllib.request.Request(req.get_full_url(),
                                                            headers=newheaders,
                                                            origin_req_host=req.origin_req_host,
                                                            unverifiable=True))

                raise urllib.request.HTTPError(req, code, msg, headers, None)

            # Some servers (e.g. GitHub archives, hosted on Amazon S3) return 403
            # Forbidden when they actually mean 405 Method Not Allowed.
            http_error_403 = http_error_405


        class FixedHTTPRedirectHandler(urllib.request.HTTPRedirectHandler):
            """
            urllib2.HTTPRedirectHandler resets the method to GET on redirect,
            when we want to follow redirects using the original method.
            """
            def redirect_request(self, req, fp, code, msg, headers, newurl):
                newreq = urllib.request.HTTPRedirectHandler.redirect_request(self, req, fp, code, msg, headers, newurl)
                newreq.get_method = req.get_method
                return newreq

        # We need to update the environment here as both the proxy and HTTPS
        # handlers need variables set. The proxy needs http_proxy and friends to
        # be set, and HTTPSHandler ends up calling into openssl to load the
        # certificates. In buildtools configurations this will be looking at the
        # wrong place for certificates by default: we set SSL_CERT_FILE to the
        # right location in the buildtools environment script but as BitBake
        # prunes prunes the environment this is lost. When binaries are executed
        # runfetchcmd ensures these values are in the environment, but this is
        # pure Python so we need to update the environment.
        #
        # Avoid tramping the environment too much by using bb.utils.environment
        # to scope the changes to the build_opener request, which is when the
        # environment lookups happen.
        newenv = bb.fetch2.get_fetcher_environment(d)

        with bb.utils.environment(**newenv):
            import ssl

            if self.check_certs(d):
                context = ssl.create_default_context()
            else:
                context = ssl._create_unverified_context()

            handlers = [FixedHTTPRedirectHandler,
                        HTTPMethodFallback,
                        urllib.request.ProxyHandler(),
                        CacheHTTPHandler(),
                        urllib.request.HTTPSHandler(context=context)]
            opener = urllib.request.build_opener(*handlers)

            try:
                uri_base = ud.url.split(";")[0]
                uri = "{}://{}{}".format(urllib.parse.urlparse(uri_base).scheme, ud.host, ud.path)
                r = urllib.request.Request(uri)
                r.get_method = lambda: "HEAD"
                # Some servers (FusionForge, as used on Alioth) require that the
                # optional Accept header is set.
                r.add_header("Accept", "*/*")
                r.add_header("User-Agent", self.user_agent)
                def add_basic_auth(login_str, request):
                    '''Adds Basic auth to http request, pass in login:password as string'''
                    import base64
                    encodeuser = base64.b64encode(login_str.encode('utf-8')).decode("utf-8")
                    authheader = "Basic %s" % encodeuser
                    r.add_header("Authorization", authheader)

                if ud.user and ud.pswd:
                    add_basic_auth(ud.user + ':' + ud.pswd, r)

                try:
                    import netrc
                    auth_data = netrc.netrc().authenticators(urllib.parse.urlparse(uri).hostname)
                    if auth_data:
                        login, _, password = auth_data
                        add_basic_auth("%s:%s" % (login, password), r)
                except (FileNotFoundError, netrc.NetrcParseError):
                    pass

                with opener.open(r, timeout=30) as response:
                    pass
            except (urllib.error.URLError, ConnectionResetError, TimeoutError) as e:
                if try_again:
                    logger.debug2("checkstatus: trying again")
                    return self.checkstatus(fetch, ud, d, False)
                else:
                    # debug for now to avoid spamming the logs in e.g. remote sstate searches
                    logger.debug2("checkstatus() urlopen failed: %s" % e)
                    return False

        return True

    def _parse_path(self, regex, s):
        """
        Find and group name, version and archive type in the given string s
        """

        m = regex.search(s)
        if m:
            pname = ''
            pver = ''
            ptype = ''

            mdict = m.groupdict()
            if 'name' in mdict.keys():
                pname = mdict['name']
            if 'pver' in mdict.keys():
                pver = mdict['pver']
            if 'type' in mdict.keys():
                ptype = mdict['type']

            bb.debug(3, "_parse_path: %s, %s, %s" % (pname, pver, ptype))

            return (pname, pver, ptype)

        return None

    def _modelate_version(self, version):
        if version[0] in ['.', '-']:
            if version[1].isdigit():
                version = version[1] + version[0] + version[2:len(version)]
            else:
                version = version[1:len(version)]

        version = re.sub('-', '.', version)
        version = re.sub('_', '.', version)
        version = re.sub('(rc)+', '.1000.', version)
        version = re.sub('(beta)+', '.100.', version)
        version = re.sub('(alpha)+', '.10.', version)
        if version[0] == 'v':
            version = version[1:len(version)]
        return version

    def _vercmp(self, old, new):
        """
        Check whether 'new' is newer than 'old' version. We use existing vercmp() for the
        purpose. PE is cleared in comparison as it's not for build, and PR is cleared too
        for simplicity as it's somehow difficult to get from various upstream format
        """

        (oldpn, oldpv, oldsuffix) = old
        (newpn, newpv, newsuffix) = new

        # Check for a new suffix type that we have never heard of before
        if newsuffix:
            m = self.suffix_regex_comp.search(newsuffix)
            if not m:
                bb.warn("%s has a possible unknown suffix: %s" % (newpn, newsuffix))
                return False

        # Not our package so ignore it
        if oldpn != newpn:
            return False

        oldpv = self._modelate_version(oldpv)
        newpv = self._modelate_version(newpv)

        return bb.utils.vercmp(("0", oldpv, ""), ("0", newpv, ""))

    def _fetch_index(self, uri, ud, d):
        """
        Run fetch checkstatus to get directory information
        """
        f = tempfile.NamedTemporaryFile()
        with tempfile.TemporaryDirectory(prefix="wget-index-") as workdir, tempfile.NamedTemporaryFile(dir=workdir, prefix="wget-listing-") as f:
            fetchcmd = self.basecmd
            fetchcmd += " -O " + f.name + " --user-agent='" + self.user_agent + "' '" + uri + "'"
            try:
                self._runwget(ud, d, fetchcmd, True, workdir=workdir)
                fetchresult = f.read()
            except bb.fetch2.BBFetchException:
                fetchresult = ""

        return fetchresult

    def _check_latest_version(self, url, package, package_regex, current_version, ud, d):
        """
        Return the latest version of a package inside a given directory path
        If error or no version, return ""
        """
        valid = 0
        version = ['', '', '']

        bb.debug(3, "VersionURL: %s" % (url))
        soup = BeautifulSoup(self._fetch_index(url, ud, d), "html.parser", parse_only=SoupStrainer("a"))
        if not soup:
            bb.debug(3, "*** %s NO SOUP" % (url))
            return ""

        for line in soup.find_all('a', href=True):
            bb.debug(3, "line['href'] = '%s'" % (line['href']))
            bb.debug(3, "line = '%s'" % (str(line)))

            newver = self._parse_path(package_regex, line['href'])
            if not newver:
                newver = self._parse_path(package_regex, str(line))

            if newver:
                bb.debug(3, "Upstream version found: %s" % newver[1])
                if valid == 0:
                    version = newver
                    valid = 1
                elif self._vercmp(version, newver) < 0:
                    version = newver
                
        pupver = re.sub('_', '.', version[1])

        bb.debug(3, "*** %s -> UpstreamVersion = %s (CurrentVersion = %s)" %
                (package, pupver or "N/A", current_version[1]))

        if valid:
            return pupver

        return ""

    def _check_latest_version_by_dir(self, dirver, package, package_regex, current_version, ud, d):
        """
        Scan every directory in order to get upstream version.
        """
        version_dir = ['', '', '']
        version = ['', '', '']

        dirver_regex = re.compile(r"(?P<pfx>\D*)(?P<ver>(\d+[\.\-_])*(\d+))")
        s = dirver_regex.search(dirver)
        if s:
            version_dir[1] = s.group('ver')
        else:
            version_dir[1] = dirver

        dirs_uri = bb.fetch.encodeurl([ud.type, ud.host,
                ud.path.split(dirver)[0], ud.user, ud.pswd, {}])
        bb.debug(3, "DirURL: %s, %s" % (dirs_uri, package))

        soup = BeautifulSoup(self._fetch_index(dirs_uri, ud, d), "html.parser", parse_only=SoupStrainer("a"))
        if not soup:
            return version[1]

        for line in soup.find_all('a', href=True):
            s = dirver_regex.search(line['href'].strip("/"))
            if s:
                sver = s.group('ver')

                # When prefix is part of the version directory it need to
                # ensure that only version directory is used so remove previous
                # directories if exists.
                #
                # Example: pfx = '/dir1/dir2/v' and version = '2.5' the expected
                # result is v2.5.
                spfx = s.group('pfx').split('/')[-1]

                version_dir_new = ['', sver, '']
                if self._vercmp(version_dir, version_dir_new) <= 0:
                    dirver_new = spfx + sver
                    path = ud.path.replace(dirver, dirver_new, True) \
                        .split(package)[0]
                    uri = bb.fetch.encodeurl([ud.type, ud.host, path,
                        ud.user, ud.pswd, {}])

                    pupver = self._check_latest_version(uri,
                            package, package_regex, current_version, ud, d)
                    if pupver:
                        version[1] = pupver

                    version_dir = version_dir_new

        return version[1]

    def _init_regexes(self, package, ud, d):
        """
        Match as many patterns as possible such as:
                gnome-common-2.20.0.tar.gz (most common format)
                gtk+-2.90.1.tar.gz
                xf86-input-synaptics-12.6.9.tar.gz
                dri2proto-2.3.tar.gz
                blktool_4.orig.tar.gz
                libid3tag-0.15.1b.tar.gz
                unzip552.tar.gz
                icu4c-3_6-src.tgz
                genext2fs_1.3.orig.tar.gz
                gst-fluendo-mp3
        """
        # match most patterns which uses "-" as separator to version digits
        pn_prefix1 = r"[a-zA-Z][a-zA-Z0-9]*([-_][a-zA-Z]\w+)*\+?[-_]"
        # a loose pattern such as for unzip552.tar.gz
        pn_prefix2 = r"[a-zA-Z]+"
        # a loose pattern such as for 80325-quicky-0.4.tar.gz
        pn_prefix3 = r"[0-9]+[-]?[a-zA-Z]+"
        # Save the Package Name (pn) Regex for use later
        pn_regex = r"(%s|%s|%s)" % (pn_prefix1, pn_prefix2, pn_prefix3)

        # match version
        pver_regex = r"(([A-Z]*\d+[a-zA-Z]*[\.\-_]*)+)"

        # match arch
        parch_regex = "-source|_all_"

        # src.rpm extension was added only for rpm package. Can be removed if the rpm
        # packaged will always be considered as having to be manually upgraded
        psuffix_regex = r"(tar\.\w+|tgz|zip|xz|rpm|bz2|orig\.tar\.\w+|src\.tar\.\w+|src\.tgz|svnr\d+\.tar\.\w+|stable\.tar\.\w+|src\.rpm)"

        # match name, version and archive type of a package
        package_regex_comp = re.compile(r"(?P<name>%s?\.?v?)(?P<pver>%s)(?P<arch>%s)?[\.-](?P<type>%s$)"
                                                    % (pn_regex, pver_regex, parch_regex, psuffix_regex))
        self.suffix_regex_comp = re.compile(psuffix_regex)

        # compile regex, can be specific by package or generic regex
        pn_regex = d.getVar('UPSTREAM_CHECK_REGEX')
        if pn_regex:
            package_custom_regex_comp = re.compile(pn_regex)
        else:
            version = self._parse_path(package_regex_comp, package)
            if version:
                package_custom_regex_comp = re.compile(
                    r"(?P<name>%s)(?P<pver>%s)(?P<arch>%s)?[\.-](?P<type>%s)" %
                    (re.escape(version[0]), pver_regex, parch_regex, psuffix_regex))
            else:
                package_custom_regex_comp = None

        return package_custom_regex_comp

    def latest_versionstring(self, ud, d):
        """
        Manipulate the URL and try to obtain the latest package version

        sanity check to ensure same name and type.
        """
        package = ud.path.split("/")[-1]
        current_version = ['', d.getVar('PV'), '']

        """possible to have no version in pkg name, such as spectrum-fw"""
        if not re.search(r"\d+", package):
            current_version[1] = re.sub('_', '.', current_version[1])
            current_version[1] = re.sub('-', '.', current_version[1])
            return (current_version[1], '')

        package_regex = self._init_regexes(package, ud, d)
        if package_regex is None:
            bb.warn("latest_versionstring: package %s don't match pattern" % (package))
            return ('', '')
        bb.debug(3, "latest_versionstring, regex: %s" % (package_regex.pattern))

        uri = ""
        regex_uri = d.getVar("UPSTREAM_CHECK_URI")
        if not regex_uri:
            path = ud.path.split(package)[0]

            # search for version matches on folders inside the path, like:
            # "5.7" in http://download.gnome.org/sources/${PN}/5.7/${PN}-${PV}.tar.gz
            dirver_regex = re.compile(r"(?P<dirver>[^/]*(\d+\.)*\d+([-_]r\d+)*)/")
            m = dirver_regex.findall(path)
            if m:
                pn = d.getVar('PN')
                dirver = m[-1][0]

                dirver_pn_regex = re.compile(r"%s\d?" % (re.escape(pn)))
                if not dirver_pn_regex.search(dirver):
                    return (self._check_latest_version_by_dir(dirver,
                        package, package_regex, current_version, ud, d), '')

            uri = bb.fetch.encodeurl([ud.type, ud.host, path, ud.user, ud.pswd, {}])
        else:
            uri = regex_uri

        return (self._check_latest_version(uri, package, package_regex,
                current_version, ud, d), '')
