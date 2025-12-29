# ASCII Kotlin Logo Renderer

![](kotlin_spin.gif)

## Running the Project

Build and run:

```bash
./gradlew installDist
./build/install/not-donut/bin/not-donut
```

## Configuration

Customize the rendering by editing [Configuration.kt](src/main/kotlin/com/app/config/Configuration.kt):

**Terminal Size:**
- `SCREEN_WIDTH` - Terminal width in characters
- `SCREEN_HEIGHT` - Terminal height in characters

**Frame Rate:**
- `FRAME_DELAY_MS` - Milliseconds between frames
  - `4L` = 240 FPS
  - `8L` = 120 FPS
  - `16L` = 60 FPS

**Other Settings:**
- `ROTATION_SPEED_X/Y` - Angular velocity for spinning
- `SCALE_X` - Field of view (higher = more zoomed in)
- `MIN_COLOR_BRIGHTNESS` - Minimum color intensity (0.0-1.0)

## How It Works

This renderer transforms 3D geometry into colored ASCII art through a multi-stage pipeline. Let's walk through rendering a single triangular face with a red to blue gradient.

---

### 🔷 **Step 1: Face Geometry Setup**

The Challenge: We have a triangle defined by three corners (vertices) $\mathbf{v}_0, \mathbf{v}_1, \mathbf{v}_2$ floating in 3D space. We need to efficiently determine which points lie on this triangle.

The Surface Normal tells us which direction the triangle is facing (imagine an arrow perpendicular to the surface):

```math
\mathbf{n} = \frac{(\mathbf{v}_1 - \mathbf{v}_0) \times (\mathbf{v}_2 - \mathbf{v}_0)}{||(\mathbf{v}_1 - \mathbf{v}_0) \times (\mathbf{v}_2 - \mathbf{v}_0)||}
```

The cross product $\times$ creates a vector perpendicular to both edges, and we normalize it to unit length.

Creating a Flat Map: Think of the triangle as a tilted piece of paper in 3D space. We create a 2D coordinate system (like a grid on that paper) using two perpendicular basis vectors $\mathbf{u}$ and $\mathbf{v}$ that lie flat on the triangle:

1. Pick an arbitrary vector $\mathbf{a}$ (avoiding parallel to $\mathbf{n}$)
2. Project out the component parallel to the normal: $\mathbf{u} = \mathbf{a} - (\mathbf{a} \cdot \mathbf{n})\mathbf{n}$, then normalize
3. Complete the orthogonal basis: $\mathbf{v} = \mathbf{n} \times \mathbf{u}$

Now any point on the face can be written as: $\mathbf{p} = \mathbf{v}_0 + \alpha\mathbf{u} + \beta\mathbf{v}$

---

### 🎯 **Step 2: Face Sampling**

The renderer checks points in a grid pattern to see which ones actually fall on the triangle.

For normalized coordinates $(x_n, y_n) \in [-1, 1]^2$:

1. Scale to bounding box: $x_f = x_{min} + \frac{(x_n + 1)(x_{max} - x_{min})}{2}$
2. Ray casting test: Imagine shining a laser pointer horizontally from a point. Count how many triangle edges it crosses. Odd count = inside, even = outside.
3. Reconstruct 3D position: $\mathbf{p} = \mathbf{v}_0 + x_f\mathbf{u} + y_f\mathbf{v}$

---

### 🌈 **Step 3: Gradient Color Calculation**

Our triangle has a color gradient from red RGB(255, 0, 0) at point $p_{start}$ to blue RGB(0, 0, 255) at point $p_{end}$.

Define the gradient direction vector:

```math
\mathbf{g} = \mathbf{p}_{end} - \mathbf{p}_{start}
```

Project the point onto the gradient: This tells us "how far along" the gradient we are (0 = start, 1 = end):

```math
t = \frac{(\mathbf{p} - \mathbf{p}_{start}) \cdot \mathbf{g}}{||\mathbf{g}||^2}
```

Blend the colors using linear interpolation (lerp):

```math
C(\mathbf{p}) = C_{start} + t(C_{end} - C_{start})
```

<details>
<summary>Example calculation</summary>

If $t = 0.5$ (halfway along the gradient):
- $R = 255 + 0.5 \times (0 - 255) = 127.5$
- $G = 0 + 0.5 \times (0 - 0) = 0$
- $B = 0 + 0.5 \times (255 - 0) = 127.5$

Result: $\text{RGB}(127, 0, 127)$ - a purple color halfway between red and blue.
</details>

---

### 🔄 **Step 4: 3D Rotation**

To animate the spinning, we apply rotation matrice to both the point and its normal vector.

X-axis rotation (pitch):

```math
R_x(\theta_x) = \begin{bmatrix} 1 & 0 & 0 \\ 0 & \cos\theta_x & -\sin\theta_x \\ 0 & \sin\theta_x & \cos\theta_x \end{bmatrix}
```

Y-axis rotation (yaw):

```math
R_y(\theta_y) = \begin{bmatrix} \cos\theta_y & 0 & \sin\theta_y \\ 0 & 1 & 0 \\ -\sin\theta_y & 0 & \cos\theta_y \end{bmatrix}
```

Combined transformation: First rotate around X, then around Y:

```math
\mathbf{p}' = R_y(\theta_y) \cdot R_x(\theta_x) \cdot \mathbf{p}
```

---

### 💡 **Step 5: Lighting Calculation**

We simulate a light source at position $\mathbf{L}$ using Lambert's cosine law.

Surfaces facing the light appear brighter. This is measured by the angle between:
- The surface normal $\hat{\mathbf{n}}'$ (which way the surface faces)
- The light direction $\hat{\mathbf{l}}$ (direction from surface to light)

```math
I = \max\left(0, \hat{\mathbf{n}}' \cdot \hat{\mathbf{l}}\right)
```

where $\hat{\mathbf{l}} = \frac{\mathbf{L} - \mathbf{p}'}{||\mathbf{L} - \mathbf{p}'||}$ and the dot product $\cdot$ computes $\cos(\text{angle})$.

> If the surface faces away from the light (angle > 90°), the dot product is negative, so we clamp to 0 (dark).

---

### 📐 **Step 6: Perspective Projection**

Now we flatten our 3D world onto a 2D screen using perspective division.

The formula: objects farther away appear smaller. We achieve this with:

```math
z_{inv} = \frac{1}{z' + z_{offset}}
```

Then compute screen coordinates:

```math
x_{screen} = \frac{W}{2} + x' \cdot s_x \cdot z_{inv}
```

```math
y_{screen} = \frac{H}{2} - y' \cdot s_y \cdot z_{inv}
```

where:
- $W, H$ = screen width/height
- $s_x, s_y$ = scale factors (field of view)
- $z_{offset}$ = camera distance (prevents division by zero)

> Why $1/z$? Far objects (large $z$) get small $z_{inv}$, pulling coordinates toward the center. Near objects get large $z_{inv}$, spreading coordinates out.

---

### 🎨 **Step 7: ASCII Rasterization**

Convert the brightness and color into colored ASCII characters.

Character selection - denser characters = brighter areas:

```math
\text{char} = \text{ramp}[\lfloor I \cdot 11 \rfloor] \quad \text{where } \text{ramp} = \texttt{".,-~:;=!*\#\$@"}
```

Color darkening - dim the RGB values based on lighting for better visuals:

```math
m = m_{min} + (1 - m_{min}) \cdot I
```

```math
C_{final} = \lfloor m \cdot C(\mathbf{p}) \rfloor
```

The $m_{min}$ threshold prevents colors from going completely black (keeps some visibility even in shadow).

Output: Wrap it in ANSI color codes: `\u001b[38;2;R;G;Bm` + char + `\u001b[0m`

---

### 🗂️ **Step 8: Z-Buffering**

The visibility problem: When multiple triangles overlap on screen, which one should we see?

Solution: For each pixel, store the depth of the closest surface drawn so far. Only update if the new surface is closer:

```math
\text{if } z_{inv} > z_{buffer}[x_{screen}, y_{screen}] \text{ then update pixel}
```

Since $z_{inv} = 1/z$, larger values = smaller $z$ = closer to camera.
