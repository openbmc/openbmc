#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import bb
import json
import subprocess

_ALWAYS_SAFE = frozenset('ABCDEFGHIJKLMNOPQRSTUVWXYZ'
                         'abcdefghijklmnopqrstuvwxyz'
                         '0123456789'
                         '_.-~()')

MISSING_OK = object()

REGISTRY = "https://registry.npmjs.org"

# we can not use urllib.parse here because npm expects lowercase
# hex-chars but urllib generates uppercase ones
def uri_quote(s, safe = '/'):
    res = ""
    safe_set = set(safe)
    for c in s:
        if c in _ALWAYS_SAFE or c in safe_set:
            res += c
        else:
            res += '%%%02x' % ord(c)
    return res

class PackageJson:
    def __init__(self, spec):
        self.__spec = spec

    @property
    def name(self):
        return self.__spec['name']

    @property
    def version(self):
        return self.__spec['version']

    @property
    def empty_manifest(self):
        return {
            'name': self.name,
            'description': self.__spec.get('description', ''),
            'versions': {},
        }

    def base_filename(self):
        return uri_quote(self.name, safe = '@')

    def as_manifest_entry(self, tarball_uri):
        res = {}

        ## NOTE: 'npm install' requires more than basic meta information;
        ## e.g. it takes 'bin' from this manifest entry but not the actual
        ## 'package.json'
        for (idx,dflt) in [('name', None),
                           ('description', ""),
                           ('version', None),
                           ('bin', MISSING_OK),
                           ('man', MISSING_OK),
                           ('scripts', MISSING_OK),
                           ('directories', MISSING_OK),
                           ('dependencies', MISSING_OK),
                           ('devDependencies', MISSING_OK),
                           ('optionalDependencies', MISSING_OK),
                           ('license', "unknown")]:
            if idx in self.__spec:
                res[idx] = self.__spec[idx]
            elif dflt == MISSING_OK:
                pass
            elif dflt != None:
                res[idx] = dflt
            else:
                raise Exception("%s-%s: missing key %s" % (self.name,
                                                           self.version,
                                                           idx))

        res['dist'] = {
            'tarball': tarball_uri,
        }

        return res

class ManifestImpl:
    def __init__(self, base_fname, spec):
        self.__base = base_fname
        self.__spec = spec

    def load(self):
        try:
            with open(self.filename, "r") as f:
                res = json.load(f)
        except IOError:
            res = self.__spec.empty_manifest

        return res

    def save(self, meta):
        with open(self.filename, "w") as f:
            json.dump(meta, f, indent = 2)

    @property
    def filename(self):
        return self.__base + ".meta"

class Manifest:
    def __init__(self, base_fname, spec):
        self.__base = base_fname
        self.__spec = spec
        self.__lockf = None
        self.__impl = None

    def __enter__(self):
        self.__lockf = bb.utils.lockfile(self.__base + ".lock")
        self.__impl  = ManifestImpl(self.__base, self.__spec)
        return self.__impl

    def __exit__(self, exc_type, exc_val, exc_tb):
        bb.utils.unlockfile(self.__lockf)

class NpmCache:
    def __init__(self, cache):
        self.__cache = cache

    @property
    def path(self):
        return self.__cache

    def run(self, type, key, fname):
        subprocess.run(['oe-npm-cache', self.__cache, type, key, fname],
                       check = True)

class NpmRegistry:
    def __init__(self, path, cache):
        self.__path = path
        self.__cache = NpmCache(cache + '/_cacache')
        bb.utils.mkdirhier(self.__path)
        bb.utils.mkdirhier(self.__cache.path)

    @staticmethod
    ## This function is critical and must match nodejs expectations
    def _meta_uri(spec):
        return REGISTRY + '/' + uri_quote(spec.name, safe = '@')

    @staticmethod
    ## Exact return value does not matter; just make it look like a
    ## usual registry url
    def _tarball_uri(spec):
        return '%s/%s/-/%s-%s.tgz' % (REGISTRY,
                                      uri_quote(spec.name, safe = '@'),
                                      uri_quote(spec.name, safe = '@/'),
                                      spec.version)

    def add_pkg(self, tarball, pkg_json):
        pkg_json = PackageJson(pkg_json)
        base = os.path.join(self.__path, pkg_json.base_filename())

        with Manifest(base, pkg_json) as manifest:
            meta = manifest.load()
            tarball_uri = self._tarball_uri(pkg_json)

            meta['versions'][pkg_json.version] = pkg_json.as_manifest_entry(tarball_uri)

            manifest.save(meta)

            ## Cache entries are a little bit dependent on the nodejs
            ## version; version specific cache implementation must
            ## mitigate differences
            self.__cache.run('meta', self._meta_uri(pkg_json), manifest.filename);
            self.__cache.run('tgz',  tarball_uri, tarball);
