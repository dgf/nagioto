# nagioto

quick and dirty monitoring solution

Supports HTTP Icinga status.cgi JSON format.

The password is explicitly stored in plain text as long as this not supports HTTPS.

Please use a dedicated monitoring user that only can read public acceptable information
like websites, DNS entries and SMTP server.

## Icinga API calls

http://docs.icinga.org/latest/en/cgiparams.html

### Status CGI params

### Alert Request

fetch all service errors 28 = 4 Warning + 8 Unknown + 16 Critical

    status.cgi?jsonoutput&style=detail&servicestatustypes=28

### Host List

fetch all host details

    status.cgi?jsonoutput&style=hostdetail

### Service List

    status.cgi?jsonoutput
