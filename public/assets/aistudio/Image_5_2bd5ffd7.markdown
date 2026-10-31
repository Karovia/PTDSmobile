---
name: Clinical Transparency
colors:
  surface: '#f9f9f9'
  surface-dim: '#dadada'
  surface-bright: '#f9f9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f3'
  surface-container: '#eeeeee'
  surface-container-high: '#e8e8e8'
  surface-container-highest: '#e2e2e2'
  on-surface: '#1a1c1c'
  on-surface-variant: '#5d3f3c'
  inverse-surface: '#2f3131'
  inverse-on-surface: '#f1f1f1'
  outline: '#926f6b'
  outline-variant: '#e6bdb8'
  surface-tint: '#c00014'
  primary: '#ae0011'
  on-primary: '#ffffff'
  primary-container: '#d71920'
  on-primary-container: '#ffece9'
  inverse-primary: '#ffb4ab'
  secondary: '#5f5e5e'
  on-secondary: '#ffffff'
  secondary-container: '#e5e2e1'
  on-secondary-container: '#656464'
  tertiary: '#545555'
  on-tertiary: '#ffffff'
  tertiary-container: '#6d6d6d'
  on-tertiary-container: '#f1f0f0'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdad6'
  primary-fixed-dim: '#ffb4ab'
  on-primary-fixed: '#410002'
  on-primary-fixed-variant: '#93000d'
  secondary-fixed: '#e5e2e1'
  secondary-fixed-dim: '#c8c6c5'
  on-secondary-fixed: '#1c1b1b'
  on-secondary-fixed-variant: '#474646'
  tertiary-fixed: '#e3e2e2'
  tertiary-fixed-dim: '#c7c6c6'
  on-tertiary-fixed: '#1b1c1c'
  on-tertiary-fixed-variant: '#464747'
  background: '#f9f9f9'
  on-background: '#1a1c1c'
  surface-variant: '#e2e2e2'
typography:
  display-hero:
    fontFamily: Space Mono
    fontSize: 56px
    fontWeight: '700'
    lineHeight: 60px
    letterSpacing: -0.04em
  display-hero-mobile:
    fontFamily: Space Mono
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.03em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 42px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 34px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 30px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Space Grotesk
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 26px
    letterSpacing: 0em
  body-lg:
    fontFamily: Space Grotesk
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: 0em
  body-md:
    fontFamily: Space Grotesk
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.01em
  body-sm:
    fontFamily: Space Grotesk
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-mono-lg:
    fontFamily: Space Mono
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 18px
    letterSpacing: 0.06em
  label-mono-md:
    fontFamily: Space Mono
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0.08em
  label-mono-sm:
    fontFamily: Space Mono
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 12px
    letterSpacing: 0.12em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-desktop: 1.5rem
  margin: 1rem
  margin-tablet: 2rem
  margin-desktop: 3rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2.5rem
---

## Brand & Style

This design system translates the raw, utilitarian ethos of industrial product engineering into an uncompromising medical monitoring environment. The experience rejects the soft pastels, patronizing cheerfulness, and vague abstractions common in consumer wellness apps. Instead, it treats patient telemetry with the gravity, diagnostic clarity, and tactile precision of high-end diagnostic hardware.

### Personality & Emotional Tenor
- **Hyper-Legible & Diagnostic:** Information density is structured with mathematical rigor. Vitals and biological warnings are presented without decorative softening.
- **Physical & Hardware-Inspired:** Interfaces behave like physical instrument panels. Elements feature tactile concentric surfaces, micro-textured dividing lines, mechanical feedback, and deliberate states.
- **Controlled & Restrained:** Surfaces remain stark and monochromatic. Color is withheld completely until it is functionally required, making critical physiological states unmistakable.

### Design Movement: Industrial Functionalism / Neo-Brutalist Minimalism
The visual language balances stark architectural lines with refined, pill-like contours. It pairs pure light tones with aggressive typographic discipline, fine technical grid alignments, hairline structural borders, and purposeful dot-matrix display cues for vital metrics.

## Colors

The palette operates under absolute chromatic discipline. Color is never decorative; it is an active diagnostic beacon.

### Base Surfaces & Canvas
- **Pure White (`#FFFFFF`):** Primary card fill, elevated clinical module surface, modal backdrop.
- **Raw Grey Canvas (`#F4F4F4`):** Canvas bedrock. Neutral, non-glare industrial background.
- **Sub-Tier Grey (`#EAEAEA`):** Recessed telemetry wells, inactive segmented switch tracks, nested telemetry beds.

### Monochromatic Structure & Contrast
- **Pitch Black (`#111111`):** Primary data readouts, dominant headers, primary active button fills, tactile toggle caps.
- **Dark Charcoal (`#222222`):** Structural borders, high-emphasis icons, secondary labels.
- **Mid Technical Greys (`#777777`, `#8E8E93`):** Metadata, axis lines, dimensional units (e.g., `BPM`, `mg/dL`, `SpO2`), timestamps.
- **Hairline Border Grey (`#D1D1D6`):** 1px mechanical container outlines and tabular dividers.

### The Diagnostic Red Accent
- **Signal Red (`#D71920` / `#EB0000`):** Reserved exclusively for physiological anomalies, critical threshold transgressions, active recording beacons, hardware connection pulses, and primary toggle active points. It must never cover large solid backgrounds. Use it purely for micro-indicators, telemetry threshold highlights, active indicators, and high-priority states.

## Typography

The type system uses a dual-engine architecture:
1. **Space Grotesk** serves as the primary structural voice for interface titles, section navigation, and descriptive copy. It delivers an architectural, proportional structure.
2. **Space Mono** serves as the clinical data layer. All dynamic medical readings, timestamps, dosages, biometric data, status badges, and hardware indicators use this monospaced engine to ensure tabular stability and a dot-matrix, instrument-panel aesthetic.

### Typographic Rules
- All numeric readouts must use monospaced figures to prevent layout shift during high-frequency live biometric updates.
- Section tags, category descriptors, and operational states must be set in uppercase via `label-mono-sm` or `label-mono-md` with expanded letter spacing (+0.08em to +0.12em).
- Clinical units (e.g., `mg/dL`, `BPM`, `kPA`, `ms`) should be set in `label-mono-sm` at 70% opacity directly adjacent to large numerical readouts.

## Layout & Spacing

The layout is grounded in a modular, telemetry-grade fluid grid. Spacing is mathematically consistent and strictly non-elastic, enforcing structural alignment across all panels.

### Structural Grid
- **Mobile (0–599px):** 4-column layout, `1rem` margins, `1rem` gutters. High vertical density; metrics pack into 2x2 modular matrix cells.
- **Tablet (600–1023px):** 8-column layout, `2rem` margins, `1rem` gutters. Vitals monitor splits into primary diagnostic scope + secondary history list.
- **Desktop (1024px+):** 12-column layout, max-width `1440px` centered, `3rem` margins, `1.5rem` gutters. Side panel fixed instrumentation shelf with fluid central graphing arrays.

### Alignment Disciplines
- Internal card padding is strictly uniform: `1.25rem` on mobile, `1.5rem` on desktop.
- Metric clusters must align to an 8px technical rhythm. Micro-spacers between data labels and values should remain locked at `0.25rem` (`space-xs`) or `0.5rem` (`space-sm`).

## Elevation & Depth

This system rejects drop shadows, atmospheric blurs, and gradient-based depths. Hierarchy is created through physical mechanical layering: precise surface-tone stacking combined with architectural hairline boundaries.

### Depth Mechanics
- **Ground Floor (Base Canvas):** Set to `#F4F4F4`. All secondary elements, background tracking paths, and contextual gutters rest here.
- **Level 1 (Clinical Modules & Instrumentation Cards):** Pure `#FFFFFF` surface bounded by a crisp `1px solid #D1D1D6` border. This creates immediate contrast without heavy dropshadows.
- **Level 2 (Recessed Telemetry Wells):** Cut into Level 1 cards using a `#EAEAEA` or `#F4F4F4` surface fill with an inner `1px solid #D1D1D6` border. Used for graph baselines, active wave feeds, and nested data fields.
- **Level 3 (Overlay Sheets & Modals):** Crisp `#FFFFFF` surface enclosed by a prominent `1px solid #111111` border.
- **Shadow Exception:** A single, sharp offset mechanical shadow is allowed only on primary floating action elements or dragged modules: `0px 4px 0px 0px #111111` or `0px 2px 8px rgba(17, 17, 17, 0.06)`. Never use diffuse, multi-layered ambient blurs.

## Shapes

The interface balances sharp, 1px internal lines with industrial, pill-shaped external silhouettes, inspired by high-end hardware casings and precision instrumentation.

### Concentric Corner Geometry
- **Outer Shell Cards:** Built using soft squircle proportions with `1.5rem` to `2rem` outer radii.
- **Nested Inner Wells:** Any container nested within an outer card must use a proportionally reduced corner radius (`outer_radius - padding_offset`), preventing awkward visual gaps and reinforcing the feeling of machined hardware.
- **Pills & Toggles:** Badges, status markers, chips, and interactive buttons use full-radius pill styling (`9999px`).
- **Hairline Enclosures:** Every shape boundary is framed by a strict 1px solid border. Outer surfaces never bleed unchecked into the background canvas.

## Components

### Buttons
- **Primary Action:** Solid `#111111` fill, `#FFFFFF` text in `Space Grotesk` medium, fully pill-shaped (`9999px`). Hover: background shifts to `#222222`. Active: micro-scale to 0.98.
- **Secondary / Metric Action:** `#FFFFFF` fill, `1px solid #111111` boundary, `#111111` typography.
- **Destructive / Urgent Stop:** Crisp `#FFFFFF` surface with a `1px solid #D71920` hairline and `#D71920` monospaced label. When active or triggered, flips to solid `#D71920` with pure white text.

### Telemetry Cards & Concentric Squircles
- Composed of a `#FFFFFF` base on `#F4F4F4` canvas, framed by a `1px solid #D1D1D6` border.
- Top card header contains an uppercase category label in `label-mono-sm` alongside a dot-matrix icon or a live status beacon.
- Card interiors feature recessed telemetry wells (`#F4F4F4`) containing high-contrast physiological charts, ECG sparklines, or numerical values.

### Status Indicators & Red Accent Beacons
- **Live Normal:** 8px circular dot, `#111111` with a subtle 12px outer ring in `#D1D1D6`.
- **Telemetry Alert / Out of Range:** 8px circular dot in `#D71920`, flashing at 1Hz (`opacity: 1` to `opacity: 0.2`) accompanied by an uppercase monospaced red alert flag (`Space Mono` bold).

### Segmented Controls & Mechanical Toggles
- **Track:** Recessed pill track with `#EAEAEA` background and a `1px solid #D1D1D6` border.
- **Active Segment / Thumb:** Solid `#FFFFFF` floating thumb with a `1px solid #111111` border and an optional 4px internal center pip in `#D71920` or `#111111`.
- **Text:** Inactive options use `#777777` in `label-mono-sm`; selected options use `#111111` in `label-mono-sm` bold.

### Input Fields
- Monospaced numeric or alphanumeric input fields built on a `#FFFFFF` fill with a `1px solid #D1D1D6` border.
- **Focus State:** Border shifts directly to `1px solid #111111` with zero fuzzy focus rings.
- **Error State:** Border shifts to `1px solid #D71920`, accompanied by a trailing red dot-matrix alert indicator.

### Medical Chips & Filter Pills
- Fully rounded pills (`9999px`) with `#FFFFFF` fill, `1px solid #D1D1D6` border, and 12px monospaced typography.
- Active state transitions the background to `#111111` with white text and a leading 4px dot in `#D71920`.

### Dot-Matrix Clinical Iconography
- Icons are drawn on a strict 24x24 grid using deliberate dot arrays or modular 1.5px monoline vectors (e.g., dot-matrix heart, running figure, stopwatch, calendar grid, ECG waves).
- Icons remain strictly monochromatic (`#111111` or `#777777`), shifting to `#D71920` only to indicate hardware disconnections or critical biometric alerts.