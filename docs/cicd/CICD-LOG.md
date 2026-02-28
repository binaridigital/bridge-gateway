# Bridge Gateway CI/CD Log

**Last updated:** 2026-02-28

## Summary

| Item | Status |
|------|--------|
| Vault GitHub AppRole | ✅ `github-bridge-gateway-dev` exists |
| Harbor secret | ✅ `kv/cicd/harbor/bridge-gateway` exists |
| CD failure cause | Vault path format (KV v2) |
| Fix applied | Updated secrets path in `cd-dev.yml` |

---

## Failure Analysis (Run 22523307853)

**Failed step:** Get Harbor credentials from Vault (step 7)

**Root cause:** Incorrect KV v2 path format for `hashicorp/vault-action`.

- **Wrong:** `kv/cicd/harbor/bridge-gateway/data/harbor-registry`
- **Correct:** `kv/data/cicd/harbor/bridge-gateway` with keys `username`, `password`

For KV v2, vault-action expects the full API path: `mount/data/secret-path`.

---

## Fix Applied

**File:** `.github/workflows/cd-dev.yml`

```yaml
# Before
kv/cicd/harbor/bridge-gateway/data/harbor-registry username | HARBOR_USERNAME ;
kv/cicd/harbor/bridge-gateway/data/harbor-registry password | HARBOR_PASSWORD ;

# After
kv/data/cicd/harbor/bridge-gateway username | HARBOR_USERNAME ;
kv/data/cicd/harbor/bridge-gateway password | HARBOR_PASSWORD ;
```

---

## Vault Verification (with root token)

```bash
export VAULT_ADDR="https://vault.binari.digital"
export VAULT_TOKEN="<your-token>"

# Verify secret exists
vault kv get kv/cicd/harbor/bridge-gateway

# Verify JWT role exists
vault list auth/jwt/role
# Should include: github-bridge-gateway-dev
```

---

## GitHub Actions Commands

```bash
# List recent runs
gh run list --repo bridge-intelligence/bridge-gateway --limit 10

# View failed run details
gh run view 22523307853 --repo bridge-intelligence/bridge-gateway

# Watch a run (replace RUN_ID with actual ID)
gh run watch RUN_ID --repo bridge-intelligence/bridge-gateway

# Re-run failed workflow
gh run rerun 22523307853 --repo bridge-intelligence/bridge-gateway --failed
```

---

## Next Steps

1. **Commit and push** the workflow fix to `dev`
2. **Monitor** the new CD run
3. **Verify** ArgoCD syncs after successful build
4. **Confirm** deployment at `bridge-gateway.service.d.bridgeintelligence.ltd`

---

## References

- [vault-action KV v2 path format](https://github.com/hashicorp/vault-action#kv-secrets-engine-version-2)
- Working example: `bridge-dlt-console` uses `kv/data/ci/harbor/bridge-dlt-console`
