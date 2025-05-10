# Security Policy

## Supported Versions

We release patches for security vulnerabilities. Which versions are eligible for receiving such patches depends on the CVSS v3.0 Rating:

| Version | Supported          | End of Support     |
| ------- | ------------------ | ------------------ |
| 2.1.x   | :white_check_mark: | Current            |
| 2.0.x   | :white_check_mark: | December 2025      |
| 1.4.x   | :warning:          | Security fixes only|
| < 1.4   | :x:                | No longer supported|

:warning: = Security updates only (Critical & High severity)  
:white_check_mark: = Full support (all security updates)  
:x: = End of life, no security updates

## Reporting a Vulnerability

We take the security of our project seriously. If you believe you have found a security vulnerability, please report it to us as described below.

### Reporting Process

1. **DO NOT** create a public GitHub issue for security vulnerabilities
2. Email your findings to: security@yourproject.com (replace with your actual email)
3. Encrypt sensitive information using our PGP key (available at: [link-to-pgp-key])
4. Alternatively, report through GitHub's private vulnerability reporting:
   - Navigate to the Security tab of this repository
   - Click "Report a vulnerability"
   - Fill out the form with detailed information

### What to Include

Please include as much of the following information as possible:

- Type of vulnerability (e.g., SQL injection, XSS, buffer overflow)
- Affected component(s) and version(s)
- Step-by-step instructions to reproduce the issue
- Proof-of-concept or exploit code (if possible)
- Impact assessment and potential attack scenarios
- Any suggested fixes or mitigations

### Response Timeline

- **Initial Response**: Within 48 hours
- **Vulnerability Confirmation**: Within 7 days
- **Resolution Timeline**: Depends on severity:
  - Critical: 1-7 days
  - High: 7-30 days
  - Medium: 30-90 days
  - Low: Best effort

### What to Expect

1. **Acknowledgment**: You'll receive an initial response confirming receipt
2. **Assessment**: We'll investigate and validate the reported vulnerability
3. **Status Updates**: Regular updates on our progress (at least weekly for high/critical issues)
4. **Resolution**: We'll work on a fix and coordinate disclosure timing
5. **Credit**: With your permission, we'll acknowledge your contribution in our release notes

## Security Update Process

1. Security patches will be released as soon as possible
2. Critical vulnerabilities may trigger immediate point releases
3. Security advisories will be published through:
   - GitHub Security Advisories
   - Project mailing list
   - Security changelog

## Disclosure Policy

- We follow a coordinated disclosure process
- Reporters will be given credit (unless they prefer to remain anonymous)
- We request a 90-day disclosure deadline for most vulnerabilities
- Critical vulnerabilities may be disclosed sooner if actively exploited

## Security-Related Configuration

Please ensure you:
- Keep dependencies up to date
- Enable two-factor authentication on your accounts
- Follow security best practices outlined in our documentation
- Regularly review access permissions

## Recognition

We maintain a [Hall of Fame](/SECURITY_HALL_OF_FAME.md) to recognize security researchers who have responsibly disclosed vulnerabilities.

## Safe Harbor

We support safe harbor for security researchers who:
- Make a good faith effort to avoid privacy violations and disruptions
- Only exploit vulnerabilities to the extent necessary to confirm them
- Do not perform actions that could impact service availability

## Contact

- Security Team Email: security@wizdevtech.com
- Security Page: https://wizdevtech.com/security
- Bug Bounty Program: [If applicable]

## PGP Key

```
-----BEGIN PGP PUBLIC KEY BLOCK-----
[Your PGP public key here]
-----END PGP PUBLIC KEY BLOCK-----
```

---

**Last Updated**: May 2025  
**Next Review**: November 2025
