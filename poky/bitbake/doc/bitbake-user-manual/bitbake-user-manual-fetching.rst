.. SPDX-License-Identifier: CC-BY-2.5

=====================
File Download Support
=====================

|

BitBake's fetch module is a standalone piece of library code that deals
with the intricacies of downloading source code and files from remote
systems. Fetching source code is one of the cornerstones of building
software. As such, this module forms an important part of BitBake.

The current fetch module is called "fetch2" and refers to the fact that
it is the second major version of the API. The original version is
obsolete and has been removed from the codebase. Thus, in all cases,
"fetch" refers to "fetch2" in this manual.

The Download (Fetch)
====================

BitBake takes several steps when fetching source code or files. The
fetcher codebase deals with two distinct processes in order: obtaining
the files from somewhere (cached or otherwise) and then unpacking those
files into a specific location and perhaps in a specific way. Getting
and unpacking the files is often optionally followed by patching.
Patching, however, is not covered by this module.

The code to execute the first part of this process, a fetch, looks
something like the following::

   src_uri = (d.getVar('SRC_URI') or "").split()
   fetcher = bb.fetch2.Fetch(src_uri, d)
   fetcher.download()

This code sets up an instance of the fetch class. The instance uses a
space-separated list of URLs from the :term:`SRC_URI`
variable and then calls the ``download`` method to download the files.

The instantiation of the fetch class is usually followed by::

   rootdir = l.getVar('WORKDIR')
   fetcher.unpack(rootdir)

This code unpacks the downloaded files to the specified by ``WORKDIR``.

.. note::

   For convenience, the naming in these examples matches the variables
   used by OpenEmbedded. If you want to see the above code in action,
   examine the OpenEmbedded class file ``base.bbclass``
   .

The :term:`SRC_URI` and ``WORKDIR`` variables are not hardcoded into the
fetcher, since those fetcher methods can be (and are) called with
different variable names. In OpenEmbedded for example, the shared state
(sstate) code uses the fetch module to fetch the sstate files.

When the ``download()`` method is called, BitBake tries to resolve the
URLs by looking for source files in a specific search order:

-  *Pre-mirror Sites:* BitBake first uses pre-mirrors to try and find
   source files. These locations are defined using the
   :term:`PREMIRRORS` variable.

-  *Source URI:* If pre-mirrors fail, BitBake uses the original URL (e.g
   from :term:`SRC_URI`).

-  *Mirror Sites:* If fetch failures occur, BitBake next uses mirror
   locations as defined by the :term:`MIRRORS` variable.

For each URL passed to the fetcher, the fetcher calls the submodule that
handles that particular URL type. This behavior can be the source of
some confusion when you are providing URLs for the :term:`SRC_URI` variable.
Consider the following two URLs::

   https://git.yoctoproject.org/git/poky;protocol=git
   git://git.yoctoproject.org/git/poky;protocol=http

In the former case, the URL is passed to the ``wget`` fetcher, which does not
understand "git". Therefore, the latter case is the correct form since the Git
fetcher does know how to use HTTP as a transport.

Here are some examples that show commonly used mirror definitions::

   PREMIRRORS ?= "\
      bzr://.*/.\*  http://somemirror.org/sources/ \
      cvs://.*/.\*  http://somemirror.org/sources/ \
      git://.*/.\*  http://somemirror.org/sources/ \
      hg://.*/.\*   http://somemirror.org/sources/ \
      osc://.*/.\*  http://somemirror.org/sources/ \
      p4://.*/.\*   http://somemirror.org/sources/ \
     svn://.*/.\*   http://somemirror.org/sources/"

   MIRRORS =+ "\
      ftp://.*/.\*   http://somemirror.org/sources/ \
      http://.*/.\*  http://somemirror.org/sources/ \
      https://.*/.\* http://somemirror.org/sources/"

It is useful to note that BitBake
supports cross-URLs. It is possible to mirror a Git repository on an
HTTP server as a tarball. This is what the ``git://`` mapping in the
previous example does.

Since network accesses are slow, BitBake maintains a cache of files
downloaded from the network. Any source files that are not local (i.e.
downloaded from the Internet) are placed into the download directory,
which is specified by the :term:`DL_DIR` variable.

File integrity is of key importance for reproducing builds. For
non-local archive downloads, the fetcher code can verify SHA-256 and MD5
checksums to ensure the archives have been downloaded correctly. You can
specify these checksums by using the :term:`SRC_URI` variable with the
appropriate varflags as follows::

   SRC_URI[md5sum] = "value"
   SRC_URI[sha256sum] = "value"

You can also specify the checksums as
parameters on the :term:`SRC_URI` as shown below::

  SRC_URI = "http://example.com/foobar.tar.bz2;md5sum=4a8e0f237e961fd7785d19d07fdb994d"

If multiple URIs exist, you can specify the checksums either directly as
in the previous example, or you can name the URLs. The following syntax
shows how you name the URIs::

   SRC_URI = "http://example.com/foobar.tar.bz2;name=foo"
   SRC_URI[foo.md5sum] = 4a8e0f237e961fd7785d19d07fdb994d

After a file has been downloaded and
has had its checksum checked, a ".done" stamp is placed in :term:`DL_DIR`.
BitBake uses this stamp during subsequent builds to avoid downloading or
comparing a checksum for the file again.

.. note::

   It is assumed that local storage is safe from data corruption. If
   this were not the case, there would be bigger issues to worry about.

If :term:`BB_STRICT_CHECKSUM` is set, any
download without a checksum triggers an error message. The
:term:`BB_NO_NETWORK` variable can be used to
make any attempted network access a fatal error, which is useful for
checking that mirrors are complete as well as other things.

If :term:`BB_CHECK_SSL_CERTS` is set to ``0`` then SSL certificate checking will
be disabled. This variable defaults to ``1`` so SSL certificates are normally
checked.

.. _bb-the-unpack:

The Unpack
==========

The unpack process usually immediately follows the download. For all
URLs except Git URLs, BitBake uses the common ``unpack`` method.

A number of parameters exist that you can specify within the URL to
govern the behavior of the unpack stage:

-  *unpack:* Controls whether the URL components are unpacked. If set to
   "1", which is the default, the components are unpacked. If set to
   "0", the unpack stage leaves the file alone. This parameter is useful
   when you want an archive to be copied in and not be unpacked.

-  *dos:* Applies to ``.zip`` and ``.jar`` files and specifies whether
   to use DOS line ending conversion on text files.

-  *striplevel:* Strip specified number of leading components (levels)
   from file names on extraction

-  *subdir:* Unpacks the specific URL to the specified subdirectory
   within the root directory.

The unpack call automatically decompresses and extracts files with ".Z",
".z", ".gz", ".xz", ".zip", ".jar", ".ipk", ".rpm". ".srpm", ".deb" and
".bz2" extensions as well as various combinations of tarball extensions.

As mentioned, the Git fetcher has its own unpack method that is
optimized to work with Git trees. Basically, this method works by
cloning the tree into the final directory. The process is completed
using references so that there is only one central copy of the Git
metadata needed.

.. _bb-fetchers:

Fetchers
========

As mentioned earlier, the URL prefix determines which fetcher submodule
BitBake uses. Each submodule can support different URL parameters, which
are described in the following sections.

.. _local-file-fetcher:

Local file fetcher (``file://``)
--------------------------------

This submodule handles URLs that begin with ``file://``. The filename
you specify within the URL can be either an absolute or relative path to
a file. If the filename is relative, the contents of the
:term:`FILESPATH` variable is used in the same way
``PATH`` is used to find executables. If the file cannot be found, it is
assumed that it is available in :term:`DL_DIR` by the
time the ``download()`` method is called.

If you specify a directory, the entire directory is unpacked.

Here are a couple of example URLs, the first relative and the second
absolute::

   SRC_URI = "file://relativefile.patch"
   SRC_URI = "file:///Users/ich/very_important_software"

.. _http-ftp-fetcher:

HTTP/FTP wget fetcher (``http://``, ``ftp://``, ``https://``)
-------------------------------------------------------------

This fetcher obtains files from web and FTP servers. Internally, the
fetcher uses the wget utility.

The executable and parameters used are specified by the
``FETCHCMD_wget`` variable, which defaults to sensible values. The
fetcher supports a parameter "downloadfilename" that allows the name of
the downloaded file to be specified. Specifying the name of the
downloaded file is useful for avoiding collisions in
:term:`DL_DIR` when dealing with multiple files that
have the same name.

If a username and password are specified in the ``SRC_URI``, a Basic
Authorization header will be added to each request, including across redirects.
To instead limit the Authorization header to the first request, add
"redirectauth=0" to the list of parameters.

Some example URLs are as follows::

   SRC_URI = "http://oe.handhelds.org/not_there.aac"
   SRC_URI = "ftp://oe.handhelds.org/not_there_as_well.aac"
   SRC_URI = "ftp://you@oe.handhelds.org/home/you/secret.plan"

.. note::

   Because URL parameters are delimited by semi-colons, this can
   introduce ambiguity when parsing URLs that also contain semi-colons,
   for example::

           SRC_URI = "http://abc123.org/git/?p=gcc/gcc.git;a=snapshot;h=a5dd47"


   Such URLs should should be modified by replacing semi-colons with '&'
   characters::

           SRC_URI = "http://abc123.org/git/?p=gcc/gcc.git&a=snapshot&h=a5dd47"


   In most cases this should work. Treating semi-colons and '&' in
   queries identically is recommended by the World Wide Web Consortium
   (W3C). Note that due to the nature of the URL, you may have to
   specify the name of the downloaded file as well::

           SRC_URI = "http://abc123.org/git/?p=gcc/gcc.git&a=snapshot&h=a5dd47;downloadfilename=myfile.bz2"


.. _cvs-fetcher:

CVS fetcher (``(cvs://``)
-------------------------

This submodule handles checking out files from the CVS version control
system. You can configure it using a number of different variables:

-  :term:`FETCHCMD_cvs <FETCHCMD>`: The name of the executable to use when running
   the ``cvs`` command. This name is usually "cvs".

-  :term:`SRCDATE`: The date to use when fetching the CVS source code. A
   special value of "now" causes the checkout to be updated on every
   build.

-  :term:`CVSDIR`: Specifies where a temporary
   checkout is saved. The location is often ``DL_DIR/cvs``.

-  CVS_PROXY_HOST: The name to use as a "proxy=" parameter to the
   ``cvs`` command.

-  CVS_PROXY_PORT: The port number to use as a "proxyport="
   parameter to the ``cvs`` command.

As well as the standard username and password URL syntax, you can also
configure the fetcher with various URL parameters:

The supported parameters are as follows:

-  *"method":* The protocol over which to communicate with the CVS
   server. By default, this protocol is "pserver". If "method" is set to
   "ext", BitBake examines the "rsh" parameter and sets ``CVS_RSH``. You
   can use "dir" for local directories.

-  *"module":* Specifies the module to check out. You must supply this
   parameter.

-  *"tag":* Describes which CVS TAG should be used for the checkout. By
   default, the TAG is empty.

-  *"date":* Specifies a date. If no "date" is specified, the
   :term:`SRCDATE` of the configuration is used to
   checkout a specific date. The special value of "now" causes the
   checkout to be updated on every build.

-  *"localdir":* Used to rename the module. Effectively, you are
   renaming the output directory to which the module is unpacked. You
   are forcing the module into a special directory relative to
   :term:`CVSDIR`.

-  *"rsh":* Used in conjunction with the "method" parameter.

-  *"scmdata":* Causes the CVS metadata to be maintained in the tarball
   the fetcher creates when set to "keep". The tarball is expanded into
   the work directory. By default, the CVS metadata is removed.

-  *"fullpath":* Controls whether the resulting checkout is at the
   module level, which is the default, or is at deeper paths.

-  *"norecurse":* Causes the fetcher to only checkout the specified
   directory with no recurse into any subdirectories.

-  *"port":* The port to which the CVS server connects.

Some example URLs are as follows::

   SRC_URI = "cvs://CVSROOT;module=mymodule;tag=some-version;method=ext"
   SRC_URI = "cvs://CVSROOT;module=mymodule;date=20060126;localdir=usethat"

.. _svn-fetcher:

Subversion (SVN) Fetcher (``svn://``)
-------------------------------------

This fetcher submodule fetches code from the Subversion source control
system. The executable used is specified by ``FETCHCMD_svn``, which
defaults to "svn". The fetcher's temporary working directory is set by
:term:`SVNDIR`, which is usually ``DL_DIR/svn``.

The supported parameters are as follows:

-  *"module":* The name of the svn module to checkout. You must provide
   this parameter. You can think of this parameter as the top-level
   directory of the repository data you want.

-  *"path_spec":* A specific directory in which to checkout the
   specified svn module.

-  *"protocol":* The protocol to use, which defaults to "svn". If
   "protocol" is set to "svn+ssh", the "ssh" parameter is also used.

-  *"rev":* The revision of the source code to checkout.

-  *"scmdata":* Causes the ".svn" directories to be available during
   compile-time when set to "keep". By default, these directories are
   removed.

-  *"ssh":* An optional parameter used when "protocol" is set to
   "svn+ssh". You can use this parameter to specify the ssh program used
   by svn.

-  *"transportuser":* When required, sets the username for the
   transport. By default, this parameter is empty. The transport
   username is different than the username used in the main URL, which
   is passed to the subversion command.

Following are three examples using svn::

   SRC_URI = "svn://myrepos/proj1;module=vip;protocol=http;rev=667"
   SRC_URI = "svn://myrepos/proj1;module=opie;protocol=svn+ssh"
   SRC_URI = "svn://myrepos/proj1;module=trunk;protocol=http;path_spec=${MY_DIR}/proj1"

.. _git-fetcher:

Git Fetcher (``git://``)
------------------------

This fetcher submodule fetches code from the Git source control system.
The fetcher works by creating a bare clone of the remote into
:term:`GITDIR`, which is usually ``DL_DIR/git2``. This
bare clone is then cloned into the work directory during the unpack
stage when a specific tree is checked out. This is done using alternates
and by reference to minimize the amount of duplicate data on the disk
and make the unpack process fast. The executable used can be set with
``FETCHCMD_git``.

This fetcher supports the following parameters:

-  *"protocol":* The protocol used to fetch the files. The default is
   "git" when a hostname is set. If a hostname is not set, the Git
   protocol is "file". You can also use "http", "https", "ssh" and
   "rsync".

   .. note::

     When ``protocol`` is "ssh", the URL expected in :term:`SRC_URI` differs
     from the one that is typically passed to ``git clone`` command and provided
     by the Git server to fetch from. For example, the URL returned by GitLab
     server for ``mesa`` when cloning over SSH is
     ``git@gitlab.freedesktop.org:mesa/mesa.git``, however the expected URL in
     :term:`SRC_URI` is the following::

       SRC_URI = "git://git@gitlab.freedesktop.org/mesa/mesa.git;branch=main;protocol=ssh;..."

     Note the ``:`` character changed for a ``/`` before the path to the project.

-  *"nocheckout":* Tells the fetcher to not checkout source code when
   unpacking when set to "1". Set this option for the URL where there is
   a custom routine to checkout code. The default is "0".

-  *"rebaseable":* Indicates that the upstream Git repository can be
   rebased. You should set this parameter to "1" if revisions can become
   detached from branches. In this case, the source mirror tarball is
   done per revision, which has a loss of efficiency. Rebasing the
   upstream Git repository could cause the current revision to disappear
   from the upstream repository. This option reminds the fetcher to
   preserve the local cache carefully for future use. The default value
   for this parameter is "0".

-  *"nobranch":* Tells the fetcher to not check the SHA validation for
   the branch when set to "1". The default is "0". Set this option for
   the recipe that refers to the commit that is valid for any namespace
   (branch, tag, ...) instead of the branch.

-  *"bareclone":* Tells the fetcher to clone a bare clone into the
   destination directory without checking out a working tree. Only the
   raw Git metadata is provided. This parameter implies the "nocheckout"
   parameter as well.

-  *"branch":* The branch(es) of the Git tree to clone. Unless
   "nobranch" is set to "1", this is a mandatory parameter. The number of
   branch parameters must match the number of name parameters.

-  *"rev":* The revision to use for the checkout. The default is
   "master".

-  *"tag":* Specifies a tag to use for the checkout. To correctly
   resolve tags, BitBake must access the network. For that reason, tags
   are often not used. As far as Git is concerned, the "tag" parameter
   behaves effectively the same as the "rev" parameter.

-  *"subpath":* Limits the checkout to a specific subpath of the tree.
   By default, the whole tree is checked out.

-  *"destsuffix":* The name of the path in which to place the checkout.
   By default, the path is ``git/``.

-  *"usehead":* Enables local ``git://`` URLs to use the current branch
   HEAD as the revision for use with ``AUTOREV``. The "usehead"
   parameter implies no branch and only works when the transfer protocol
   is ``file://``.

Here are some example URLs::

   SRC_URI = "git://github.com/fronteed/icheck.git;protocol=https;branch=${PV};tag=${PV}"
   SRC_URI = "git://github.com/asciidoc/asciidoc-py;protocol=https;branch=main"
   SRC_URI = "git://git@gitlab.freedesktop.org/mesa/mesa.git;branch=main;protocol=ssh;..."

.. note::

   When using ``git`` as the fetcher of the main source code of your software,
   ``S`` should be set accordingly::

       S = "${WORKDIR}/git"

.. note::

   Specifying passwords directly in ``git://`` urls is not supported.
   There are several reasons: :term:`SRC_URI` is often written out to logs and
   other places, and that could easily leak passwords; it is also all too
   easy to share metadata without removing passwords. SSH keys, ``~/.netrc``
   and ``~/.ssh/config`` files can be used as alternatives.


.. _gitsm-fetcher:

Git Submodule Fetcher (``gitsm://``)
------------------------------------

This fetcher submodule inherits from the :ref:`Git
fetcher<bitbake-user-manual/bitbake-user-manual-fetching:git fetcher
(\`\`git://\`\`)>` and extends that fetcher's behavior by fetching a
repository's submodules. :term:`SRC_URI` is passed to the Git fetcher as
described in the :ref:`bitbake-user-manual/bitbake-user-manual-fetching:git
fetcher (\`\`git://\`\`)` section.

.. note::

   You must clean a recipe when switching between '``git://``' and
   '``gitsm://``' URLs.

   The Git Submodules fetcher is not a complete fetcher implementation.
   The fetcher has known issues where it does not use the normal source
   mirroring infrastructure properly. Further, the submodule sources it
   fetches are not visible to the licensing and source archiving
   infrastructures.

.. _clearcase-fetcher:

ClearCase Fetcher (``ccrc://``)
-------------------------------

This fetcher submodule fetches code from a
`ClearCase <http://en.wikipedia.org/wiki/Rational_ClearCase>`__
repository.

To use this fetcher, make sure your recipe has proper
:term:`SRC_URI`, :term:`SRCREV`, and
:term:`PV` settings. Here is an example::

   SRC_URI = "ccrc://cc.example.org/ccrc;vob=/example_vob;module=/example_module"
   SRCREV = "EXAMPLE_CLEARCASE_TAG"
   PV = "${@d.getVar("SRCREV", False).replace("/", "+")}"

The fetcher uses the ``rcleartool`` or
``cleartool`` remote client, depending on which one is available.

Following are options for the :term:`SRC_URI` statement:

-  *vob*: The name, which must include the prepending "/" character,
   of the ClearCase VOB. This option is required.

-  *module*: The module, which must include the prepending "/"
   character, in the selected VOB.

   .. note::

      The module and vob options are combined to create the load rule in the
      view config spec. As an example, consider the vob and module values from
      the SRC_URI statement at the start of this section. Combining those values
      results in the following::

         load /example_vob/example_module

-  *proto*: The protocol, which can be either ``http`` or ``https``.

By default, the fetcher creates a configuration specification. If you
want this specification written to an area other than the default, use
the ``CCASE_CUSTOM_CONFIG_SPEC`` variable in your recipe to define where
the specification is written.

.. note::

   the SRCREV loses its functionality if you specify this variable. However,
   SRCREV is still used to label the archive after a fetch even though it does
   not define what is fetched.

Here are a couple of other behaviors worth mentioning:

-  When using ``cleartool``, the login of ``cleartool`` is handled by
   the system. The login require no special steps.

-  In order to use ``rcleartool`` with authenticated users, an
   "rcleartool login" is necessary before using the fetcher.

.. _perforce-fetcher:

Perforce Fetcher (``p4://``)
----------------------------

This fetcher submodule fetches code from the
`Perforce <https://www.perforce.com/>`__ source control system. The
executable used is specified by ``FETCHCMD_p4``, which defaults to "p4".
The fetcher's temporary working directory is set by
:term:`P4DIR`, which defaults to "DL_DIR/p4".
The fetcher does not make use of a perforce client, instead it
relies on ``p4 files`` to retrieve a list of
files and ``p4 print`` to transfer the content
of those files locally.

To use this fetcher, make sure your recipe has proper
:term:`SRC_URI`, :term:`SRCREV`, and
:term:`PV` values. The p4 executable is able to use the
config file defined by your system's ``P4CONFIG`` environment variable
in order to define the Perforce server URL and port, username, and
password if you do not wish to keep those values in a recipe itself. If
you choose not to use ``P4CONFIG``, or to explicitly set variables that
``P4CONFIG`` can contain, you can specify the ``P4PORT`` value, which is
the server's URL and port number, and you can specify a username and
password directly in your recipe within :term:`SRC_URI`.

Here is an example that relies on ``P4CONFIG`` to specify the server URL
and port, username, and password, and fetches the Head Revision::

   SRC_URI = "p4://example-depot/main/source/..."
   SRCREV = "${AUTOREV}"
   PV = "p4-${SRCPV}"
   S = "${WORKDIR}/p4"

Here is an example that specifies the server URL and port, username, and
password, and fetches a Revision based on a Label::

   P4PORT = "tcp:p4server.example.net:1666"
   SRC_URI = "p4://user:passwd@example-depot/main/source/..."
   SRCREV = "release-1.0"
   PV = "p4-${SRCPV}"
   S = "${WORKDIR}/p4"

.. note::

   You should always set S to "${WORKDIR}/p4" in your recipe.

By default, the fetcher strips the depot location from the local file paths. In
the above example, the content of ``example-depot/main/source/`` will be placed
in ``${WORKDIR}/p4``.  For situations where preserving parts of the remote depot
paths locally is desirable, the fetcher supports two parameters:

- *"module":*
    The top-level depot location or directory to fetch. The value of this
    parameter can also point to a single file within the depot, in which case
    the local file path will include the module path.
- *"remotepath":*
    When used with the value "``keep``", the fetcher will mirror the full depot
    paths locally for the specified location, even in combination with the
    ``module`` parameter.

Here is an example use of the the ``module`` parameter::

   SRC_URI = "p4://user:passwd@example-depot/main;module=source/..."

In this case, the content of the top-level directory ``source/`` will be fetched
to ``${P4DIR}``, including the directory itself.  The top-level directory will
be accesible at ``${P4DIR}/source/``.

Here is an example use of the the ``remotepath`` parameter::

   SRC_URI = "p4://user:passwd@example-depot/main;module=source/...;remotepath=keep"

In this case, the content of the top-level directory ``source/`` will be fetched
to ``${P4DIR}``, but the complete depot paths will be mirrored locally. The
top-level directory will be accessible at
``${P4DIR}/example-depot/main/source/``.

.. _repo-fetcher:

Repo Fetcher (``repo://``)
--------------------------

This fetcher submodule fetches code from ``google-repo`` source control
system. The fetcher works by initiating and syncing sources of the
repository into :term:`REPODIR`, which is usually
``${DL_DIR}/repo``.

This fetcher supports the following parameters:

-  *"protocol":* Protocol to fetch the repository manifest (default:
   git).

-  *"branch":* Branch or tag of repository to get (default: master).

-  *"manifest":* Name of the manifest file (default: ``default.xml``).

Here are some example URLs::

   SRC_URI = "repo://REPOROOT;protocol=git;branch=some_branch;manifest=my_manifest.xml"
   SRC_URI = "repo://REPOROOT;protocol=file;branch=some_branch;manifest=my_manifest.xml"

.. _az-fetcher:

Az Fetcher (``az://``)
--------------------------

This submodule fetches data from an
`Azure Storage account <https://docs.microsoft.com/en-us/azure/storage/>`__ ,
it inherits its functionality from the HTTP wget fetcher, but modifies its
behavior to accomodate the usage of a
`Shared Access Signature (SAS) <https://docs.microsoft.com/en-us/azure/storage/common/storage-sas-overview>`__
for non-public data.

Such functionality is set by the variable:

-  :term:`AZ_SAS`: The Azure Storage Shared Access Signature provides secure
   delegate access to resources, if this variable is set, the Az Fetcher will
   use it when fetching artifacts from the cloud.

You can specify the AZ_SAS variable as shown below::

   AZ_SAS = "se=2021-01-01&sp=r&sv=2018-11-09&sr=c&skoid=<skoid>&sig=<signature>"

Here is an example URL::

   SRC_URI = "az://<azure-storage-account>.blob.core.windows.net/<foo_container>/<bar_file>"

It can also be used when setting mirrors definitions using the :term:`PREMIRRORS` variable.

.. _crate-fetcher:

Crate Fetcher (``crate://``)
----------------------------

This submodule fetches code for
`Rust language "crates" <https://doc.rust-lang.org/reference/glossary.html?highlight=crate#crate>`__
corresponding to Rust libraries and programs to compile. Such crates are typically shared
on https://crates.io/ but this fetcher supports other crate registries too.

The format for the :term:`SRC_URI` setting must be::

   SRC_URI = "crate://REGISTRY/NAME/VERSION"

Here is an example URL::

   SRC_URI = "crate://crates.io/glob/0.2.11"

.. _npm-fetcher:

NPM Fetcher (``npm://``)
------------------------

This submodule fetches source code from an
`NPM <https://en.wikipedia.org/wiki/Npm_(software)>`__
Javascript package registry.

The format for the :term:`SRC_URI` setting must be::

   SRC_URI = "npm://some.registry.url;ParameterA=xxx;ParameterB=xxx;..."

This fetcher supports the following parameters:

-  *"package":* The NPM package name. This is a mandatory parameter.

-  *"version":* The NPM package version. This is a mandatory parameter.

-  *"downloadfilename":* Specifies the filename used when storing the downloaded file.

-  *"destsuffix":* Specifies the directory to use to unpack the package (default: ``npm``).

Note that NPM fetcher only fetches the package source itself. The dependencies
can be fetched through the `npmsw-fetcher`_.

Here is an example URL with both fetchers::

   SRC_URI = " \
       npm://registry.npmjs.org/;package=cute-files;version=${PV} \
       npmsw://${THISDIR}/${BPN}/npm-shrinkwrap.json \
       "

See :yocto_docs:`Creating Node Package Manager (NPM) Packages
</dev-manual/common-tasks.html#creating-node-package-manager-npm-packages>`
in the Yocto Project manual for details about using
:yocto_docs:`devtool <https://docs.yoctoproject.org/ref-manual/devtool-reference.html>`
to automatically create a recipe from an NPM URL.

.. _npmsw-fetcher:

NPM shrinkwrap Fetcher (``npmsw://``)
-------------------------------------

This submodule fetches source code from an
`NPM shrinkwrap <https://docs.npmjs.com/cli/v8/commands/npm-shrinkwrap>`__
description file, which lists the dependencies
of an NPM package while locking their versions.

The format for the :term:`SRC_URI` setting must be::

   SRC_URI = "npmsw://some.registry.url;ParameterA=xxx;ParameterB=xxx;..."

This fetcher supports the following parameters:

-  *"dev":* Set this parameter to ``1`` to install "devDependencies".

-  *"destsuffix":* Specifies the directory to use to unpack the dependencies
   (``${S}`` by default).

Note that the shrinkwrap file can also be provided by the recipe for
the package which has such dependencies, for example::

   SRC_URI = " \
       npm://registry.npmjs.org/;package=cute-files;version=${PV} \
       npmsw://${THISDIR}/${BPN}/npm-shrinkwrap.json \
       "

Such a file can automatically be generated using
:yocto_docs:`devtool <https://docs.yoctoproject.org/ref-manual/devtool-reference.html>`
as described in the :yocto_docs:`Creating Node Package Manager (NPM) Packages
</dev-manual/common-tasks.html#creating-node-package-manager-npm-packages>`
section of the Yocto Project.

Other Fetchers
--------------

Fetch submodules also exist for the following:

-  Bazaar (``bzr://``)

-  Mercurial (``hg://``)

-  OSC (``osc://``)

-  Secure FTP (``sftp://``)

-  Secure Shell (``ssh://``)

-  Trees using Git Annex (``gitannex://``)

No documentation currently exists for these lesser used fetcher
submodules. However, you might find the code helpful and readable.

Auto Revisions
==============

We need to document ``AUTOREV`` and :term:`SRCREV_FORMAT` here.
