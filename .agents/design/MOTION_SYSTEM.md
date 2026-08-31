# MyVault — Motion System & Physical Animation Standards

## 1. Motion Philosophy: "Instant, Functional & Tactile"

In a personal security vault, motion exists solely to **communicate spatial relationships, confirm user intent, and deliver tactile confirmation**. Sluggish or theatrical animations irritate users who are in the middle of a checkout or login flow.

```
┌─────────────────────────────────────────────────────────────┐
│                      MOTION DOCTRINE                        │
│   • Frequent Operations (Search, PIN, Copy) ──> 0ms delay   │
│   • Micro-Interactions (Button Press, Check) ──> <160ms     │
│   • Screen & Modal Transitions              ──> 200–250ms   │
│   • No Sluggish Easing (Zero Ease-In)       ──> Snappy Ease │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Animation Timing & Easing Matrix

| Interaction | Duration | Easing Curve / Physics Spec | Visual Behavior |
| :--- | :---: | :--- | :--- |
| **Button Press Scale** | `120ms` | `spring(dampingRatio = 0.6f, stiffness = Medium)` | Scales down from `1.0f` to `0.95f` on press. |
| **Copy Success Morph** | `150ms` | `FastOutSlowInEasing` | `ContentCopy` icon crossfades to `Check` (Mint). |
| **Screen Slide Navigation**| `220ms` | `FastOutSlowInEasing` | Horizontal slide (30% offset) + subtle 0.8f fade. |
| **Bottom Sheet Entrance** | `250ms` | `CubicBezier(0.2, 0.0, 0.0, 1.0)` | Upward slide from bottom edge with scrim fade. |
| **Overlay Expand** | `180ms` | `spring(dampingRatio = 0.75f, stiffness = Medium)`| Bubble expands outwards into 92% search panel. |
| **Overlay Auto-Collapse**| `140ms` | `FastOutLinearInEasing` | Panel collapses immediately back into side bubble. |
| **PIN Error Shake** | `300ms` | 4-cycle horizontal oscillation (`±12dp`) | Rapid left-right shake on indicator dots. |
| **In-Memory Search Filter**| **0ms** | Instant (`snap()`) | List re-renders immediately with zero tweening. |
| **Keypad Number Tap** | **0ms** | Immediate Canvas Fill | PIN dot fills instantly on keydown. |

---

## 3. The 3 Hard Animation Laws

### Law 1: Never Animate Keyboard or Search Interactions (0ms)
Typing into the search bar or entering a 6-digit PIN must feel instantaneous. Animating each keystroke introduces perceived lag and causes missed taps.

### Law 2: Zero Ease-In on Interface Entrances
`EaseIn` curves start slow and accelerate at the end, making UI elements feel sluggish. All entrances must use **`EaseOut` or `FastOutSlowIn`**, presenting immediate visual response at the exact moment the user taps.

### Law 3: Touch Feedback via Physics Springs
Buttons and cards must feel mechanically physical. Pressing a card applies a spring-backed depression (`scale(0.97f)`); releasing it snaps it back with zero overshoot.

---

## 4. Accessibility: Reduced Motion Support

MyVault respects the Android OS system-level setting for **"Remove animations" / "Reduced motion"**:
* When reduced motion is detected, all duration-based slides, shakes, and scale tweens are instantly disabled (`snap()`).
* State transitions become instantaneous cuts to prevent vestibular discomfort for sensitive users.
