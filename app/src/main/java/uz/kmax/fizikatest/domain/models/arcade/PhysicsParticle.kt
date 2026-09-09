package uz.kmax.fizikatest.domain.models.arcade

/**
 * Fizika o'yinidagi zarracha yoki birlikni ifodalaydi.
 * @param atomicNumber - Zarracha darajasi. O'yin logikasida kalit.
 * @param symbol - Qisqartma belgisi (masalan, "q", "e-", "p+").
 * @param nameUz - O'zbek tilidagi nomi.
 * @param color - Tile uchun fon rangi (ARGB format).
 */
data class PhysicsParticle(
    val atomicNumber: Int,
    val symbol: String,
    val nameUz: String,
    val color: Long
)

/**
 * O'yindagi 12 darajali zarrachalar zanjiri.
 * Quark(1) -> Lepton(2) -> Electron(4) -> Proton(8) -> Neutron(16) -> Atom(32) ->
 * Molecule(64) -> Matter(128) -> Planet(256) -> Star(512) -> Galaxy(1024) -> Universe(2048)
 */
object PhysicsParticleTable {

    val PARTICLES = mapOf(
        1    to PhysicsParticle(1,    "q",   "Quark",     0xFF90CAF9L),
        2    to PhysicsParticle(2,    "l",   "Lepton",    0xFF64B5F6L),
        4    to PhysicsParticle(4,    "e-",  "Electron",  0xFF42A5F5L),
        8    to PhysicsParticle(8,    "p+",  "Proton",    0xFF2196F3L),
        16   to PhysicsParticle(16,   "n",   "Neutron",   0xFF1E88E5L),
        32   to PhysicsParticle(32,   "A",   "Atom",      0xFF1976D2L),
        64   to PhysicsParticle(64,   "M",   "Molecule",  0xFF9575CDL),
        128  to PhysicsParticle(128,  "Ma",  "Matter",    0xFF7E57C2L),
        256  to PhysicsParticle(256,  "Pl",  "Planet",    0xFF673AB7L),
        512  to PhysicsParticle(512,  "St",  "Star",      0xFF5E35B1L),
        1024 to PhysicsParticle(1024, "Gx",  "Galaxy",    0xFF512DA8L),
        2048 to PhysicsParticle(2048, "U",   "Universe",  0xFF4527A0L)
    )

    fun getParticle(value: Int): PhysicsParticle? = PARTICLES[value]

    fun isWinningParticle(value: Int) = value >= 2048

    fun getTextColor(value: Int): Long =
        if (value <= 4) 0xFF333333L else 0xFFFFFFFFL
}
