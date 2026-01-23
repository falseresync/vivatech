package falseresync.vivatech.lib.math;

import org.apache.commons.lang3.tuple.Pair;
import org.joml.Math;
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
     * Orthogonal component method rotation
     * @return Vector a rotated about the vector b by the given angle
     * @implNote <a href="https://math.stackexchange.com/questions/511370/how-to-rotate-one-vector-about-another">Orthogonal component method</a>
     */
    public static Vector3f rotateAbout(Vector3f a, Vector3f b, float angle) {
        var aRejection = vectorRejection(a, b);
        var aRejectionLength = aRejection.length();
        var w = b.cross(aRejection, new Vector3f());
        return aRejection
                .mul(Math.cos(angle) / aRejectionLength, new Vector3f())
                .add(w.mul(Math.sin(angle) / w.length(), new Vector3f()))
                .mul(aRejectionLength).add(vectorProjection(a, b));
    }

    /**
     * @implNote <a href="https://en.wikipedia.org/wiki/Vector_projection">Vectors projection</a>
     */
    public static Vector3f vectorRejection(Vector3f a, Vector3f b) {
        return a.sub(vectorProjection(a, b), new Vector3f());
    }

    /**
     * @implNote <a href="https://en.wikipedia.org/wiki/Vector_projection">Vectors projection</a>
     */
    public static Vector3f vectorProjection(Vector3f a, Vector3f b) {
        return b.mul(scalarProjection(a, b), new Vector3f());
    }

    /**
     * @implNote <a href="https://en.wikipedia.org/wiki/Vector_projection">Vectors projection</a>
     */
    public static float scalarProjection(Vector3f a, Vector3f b) {
        return a.length() * a.angleCos(b);
    }
}
