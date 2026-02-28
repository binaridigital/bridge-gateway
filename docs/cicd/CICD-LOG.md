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

**Root cause:** The workflow used `jwtGithubAudience: https://github.com/bridge-intelligence` but the Vault role expects `bound_audiences: [https://github.com/binaridigital]`.

**Fix:** Update workflow to use `jwtGithubAudience: https://github.com/binaridigital` to match Vault.

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
