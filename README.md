# Bridge Gateway

**API gateway, routing, and policy enforcement** for the Bridge ecosystem. Single entry point for external clients, Binari Orbit compliance integration, rate limiting, and request routing to bridge-orchestra, bridge-custody-api, bridge-id, and other backend services.

## Overview

- **API Gateway**: Routes requests to Bridge services (orchestra, custody, identity)
- **Orbit Integration**: Compliance checks, KYC/AML gating via Binari Orbit
- **Rate limiting & auth**: Centralized API key validation, JWT verification
- **Observability**: Request tracing, metrics, audit logging

## Status

Initial repository scaffolding. Implementation pending.

## Related

- [bridge-orchestra](../bridge-orchestra) — Control plane and API layer
- [bridge-custody-api](../bridge-custody-api) — Custodial wallet primitives
- [bridge-id](../bridge-id) — Identity and KYC
- [bridge-weaver-system](../bridge-weaver-system) — Workflow coordination
- [docs/VISION_AND_STRATEGY.md](../docs/VISION_AND_STRATEGY.md) — Gateway strategy

## Repo structure

```
bridge-gateway/
├── README.md
├── .gitignore
└── env/
    └── dev.env.example
```

## License

Proprietary — Binari Digital / Bridge ecosystem.
