package falseresync.lib.math;

import org.apache.commons.lang3.tuple.Pair;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VectorMath {
    /**
     * Decomposes a rotation quaternion into 2 components, representing a swing rotation component
     * to the direction (the rotation of the direction vector itself) and a twist around the direction
     * (the rotation only around the direction vector)
     *
     * @return A pair of quaternions: swing and twist
     * @implNote Swing-twist quaternion decomposition in <a href="https://stackoverflow.com/a/22401169">pseudocode</a>
     * and <a href="http://www.euclideanspace.com/maths/geometry/rotations/for/decomposition/">mathematically</a>
     */
    public static Pair<Quaternionf, Quaternionf> swingTwistDecomposition(Quaternionf rotation, Vector3f direction) {
        var rotationAxis = new Vector3f(rotation.x, rotation.y, rotation.z);
        var projection = vectorProjection(rotationAxis, direction);
        var twist = new Quaternionf(projection.x, projection.y, projection.z, rotation.w).normalize();
        var swing = rotation.mul(twist.conjugate(new Quaternionf()), new Quaternionf());
        return Pair.ofNonNull(swing, twist);
    }

    /**
     * @implNote <a href="https://en.wikipedia.org/wiki/Vector_projection">Vectors projection</a>
     */
    public static Vector3f vectorProjection(Vector3f a, Vector3f b) {
        return b.mul(scalarProjection(a, b));
    }

    /**
     * @implNote <a href="https://en.wikipedia.org/wiki/Vector_projection">Vectors projection</a>
     */
    public static float scalarProjection(Vector3f a, Vector3f b) {
        return a.length() * a.angleCos(b);
    }
}
