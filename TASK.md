# Task: Redeem points

You'll work on this on your own for **60 minutes**. When we're back, you'll walk us through what you built, how you built it, and what you'd do next.

Use AI tools exactly as you would in your daily work. We're as interested in **how** you plan, guide the tools and check the result as in the code itself.

## The feature

Members can spend points on a reward.

```
POST /accounts/{accountId}/redemptions
Idempotency-Key: <client-generated unique key>
Content-Type: application/json

{ "points": 300, "rewardCode": "COFFEE-SMALL" }
```

There's no reward catalogue: take `points` and `rewardCode` from the request as given.

### Responses

| Status | When | Body |
|---|---|---|
| `201 Created` | Redemption done | `{ "redemptionId", "accountId", "points", "rewardCode", "balanceAfter" }` |
| `400 Bad Request` | `Idempotency-Key` missing, `points` ≤ 0, or `rewardCode` blank | problem details |
| `404 Not Found` | Account doesn't exist | problem details |
| `422 Unprocessable Entity` | Not enough points | problem details |
| `409 Conflict` | Same `Idempotency-Key` reused with a **different** request body (optionally also a duplicate that arrives while the original is still in progress, see rule 2) | problem details |

### Business rules

1. **The balance must never go below zero**, including when many redemption requests for the same account arrive at the same time. Under contention every request still gets a definite answer: if the balance covers it, it succeeds; if not, `422`. Don't fail a request only because another one touched the account at the same time.
2. **A retry must not redeem twice.** A repeated request with the same `Idempotency-Key` and the same body returns the original result (`200` or `201`, same `redemptionId`) and does not deduct points again. This also applies when the duplicates arrive at the same time: a duplicate that arrives while the original is still running may either wait and return the original result, or get `409`.
3. Every successful redemption is recorded in `points_transaction` with type `REDEEM`.

Assume the service runs as many instances behind a load balancer, so in-memory locks are not enough.

## What we expect

- Working endpoint that follows the rules above
- Tests that **prove** rules 1 and 2, including at least one test with concurrent requests
- Schema changes in `schema.sql` if you need them

Prioritise correctness over completeness. If you run out of time, a correct, well-tested core beats a feature-complete endpoint with gaps.

You can't ask us questions during the solo hour. Where this spec is silent, pick a sensible behaviour, keep going, and note the choice in `NOTES.md`.

## While you work (please do this)

1. **Work in your fork, on a branch, and commit as you go.** Fork this repository, clone your fork, create a branch from `main` (for example `redemptions`) and push it early. Commit at each meaningful step (for example: plan, tests, implementation, fixes) with a short message. Don't squash.
2. **Keep `NOTES.md` up to date** (template is in the repo). Short bullet points are fine:
   - your plan and the key decisions you made
   - how you used AI: which tool, what you asked it, where it was wrong and how you noticed
   - what you would do with more time
3. If your AI tool can export the conversation, save it in `ai-log/` (optional, but helpful).

## Before we're back: open a pull request

Push your branch to your fork and open a pull request against `main` of this repository, as you would when contributing to any public project. We'll review it together.

- Keep the description short: what you built, why it's safe when requests arrive at the same time, how you tested it, and what's left. `NOTES.md` can carry the detail.
- Leave the app compiling and `mvn test` runnable. We'll check out your pull request, run your tests and a few of our own, and then talk it through.
