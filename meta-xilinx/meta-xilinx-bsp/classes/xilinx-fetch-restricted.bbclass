# This class is setup to override the default fetching for the target recipe.
# When fetching it forces PREMIRROR only fetching so that no attempts are made
# to fetch the Xilinx downloads that are restricted to authenticated users only.
#
# The purpose of this class is to allow for automatation with pre-downloaded
# content or content that is available with curated/user defined pre-mirrors
# and or pre-populated downloads/ directories.

python do_fetch() {
    xilinx_restricted_url = "xilinx.com/member/forms/download"

    src_uri = (d.getVar('SRC_URI') or "").split()
    if len(src_uri) == 0:
        return

    for i in src_uri:
        if xilinx_restricted_url in i:
            # force the use of premirrors only, do not attempt download from xilinx.com
            d.setVar("BB_FETCH_PREMIRRORONLY", "1")
            break

    try:
        fetcher = bb.fetch2.Fetch(src_uri, d)
        fetcher.download()
    except bb.fetch2.NetworkAccess as e:
        if xilinx_restricted_url in e.url:
            # fatal on access to xilinx.com restricted downloads, print the url for manual download
            bb.fatal("The following download cannot be fetched automatically. " \
                "Please manually download the file and place it in the 'downloads' directory (or on an available PREMIRROR).\n" \
                "  %s" % (e.url.split(";")[0]))
        else:
            bb.fatal(str(e))
    except bb.fetch2.BBFetchException as e:
        bb.fatal(str(e))
}
