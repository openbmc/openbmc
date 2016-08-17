from __future__ import print_function
import sys

import httplib2
import config
import urllist


config.logger.info("Testing %s with %s", config.TOASTER_BASEURL, config.W3C_VALIDATOR)

def validate_html5(url):
    http_client = httplib2.Http(None)
    status = "Failed"
    errors = -1
    warnings = -1

    urlrequest = config.W3C_VALIDATOR+url

    # pylint: disable=broad-except
    # we disable the broad-except because we want to actually catch all possible exceptions
    try:
        resp, _ = http_client.request(urlrequest, "HEAD")
        if resp['x-w3c-validator-status'] != "Abort":
            status = resp['x-w3c-validator-status']
            errors = int(resp['x-w3c-validator-errors'])
            warnings = int(resp['x-w3c-validator-warnings'])

            if status == 'Invalid':
                config.logger.warn("Failed %s is %s\terrors %s warnings %s (check at %s)", url, status, errors, warnings, urlrequest)
            else:
                config.logger.debug("OK! %s", url)

    except Exception as exc:
        config.logger.warn("Failed validation call: %s", exc)
    return (status, errors, warnings)


def print_validation(url):
    status, errors, warnings = validate_html5(url)
    config.logger.error("url %s is %s\terrors %s warnings %s (check at %s)", url, status, errors, warnings, config.W3C_VALIDATOR+url)


def main():
    print("Testing %s with %s" % (config.TOASTER_BASEURL, config.W3C_VALIDATOR))

    if len(sys.argv) > 1:
        print_validation(sys.argv[1])
    else:
        for url in urllist.URLS:
            print_validation(config.TOASTER_BASEURL+url)

if __name__ == "__main__":
    main()
