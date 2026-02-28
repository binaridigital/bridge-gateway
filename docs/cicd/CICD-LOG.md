# Bridge Gateway CI/CD Log

**Last updated:** 2026-02-28

## Summary

| Item | Status |
|------|--------|
| Vault JWT role | ✅ `github-bridge-gateway-dev` exists |
| Harbor secret | ✅ `kv/cicd/harbor/bridge-gateway` exists |
| CD failure cause | **JWT audience mismatch** (not path) |
| Fix required | Add `bound_audiences` to Vault role |

---

## Actual Root Cause (Run 22525507847)

**Failed step:** Get Harbor credentials from Vault (step 7)

**Error message:**
```
error validating token: invalid audience (aud) claim: 
audience claim does not match any expected audience
```

**Root cause:** The Vault JWT role `github-bridge-gateway-dev` does not have `bound_audiences` configured to accept the GitHub OIDC token's audience. The workflow uses `jwtGithubAudience: https://github.com/bridge-intelligence`, so the role must include this in `bound_audiences`.

---

## Vault Fix (run with root token)

```bash
export VAULT_ADDR="https://vault.binari.digital"
export VAULT_TOKEN="<your-root-token>"

# 1. Inspect current role config
vault read auth/jwt/role/github-bridge-gateway-dev

# 2. Update role to add bound_audiences (merge with existing config)
vault write auth/jwt/role/github-bridge-gateway-dev \
  bound_audiences="https://github.com/bridge-intelligence" \
  user_claim="actor" \
  role_type="jwt" \
  policies="<existing-policy-name>" \
  bound_claims_type="glob" \
  bound_claims='{"repository":"bridge-intelligence/bridge-gateway"}'
```

**Note:** Replace `<existing-policy-name>` with the policy currently attached (from step 1). If the role has other settings (e.g. `token_ttl`), include them in the write.

**Alternative:** If `github-bridge-orchestra-dev` works, copy its config:
```bash
vault read auth/jwt/role/github-bridge-orchestra-dev
# Then replicate for github-bridge-gateway-dev with bound_claims for bridge-gateway repo
```

---

## Vault Verification (with root token)

```bash
export VAULT_ADDR="https://vault.binari.digital"
export VAULT_TOKEN="<your-token>"

# Verify secret exists
vault kv get kv/cicd/harbor/bridge-gateway

# Verify JWT role exists and has bound_audiences
vault read auth/jwt/role/github-bridge-gateway-dev
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
