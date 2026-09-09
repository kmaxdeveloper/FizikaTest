package uz.kmax.fizikatest.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.database.*
import com.google.firebase.database.database

class FirebaseManager() {

    val database = Firebase.database.getReference("FizikaTest")

    // Bir martalik ma'lumot o'qish
    fun <T> readData(path: String, clazz: Class<T>, onComplete: (T?, String?) -> Unit) {
        database.child(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(clazz)
                onComplete(data, null)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(null, error.message)
            }
        })
    }

    fun observeListVisibly(path: String, onDataChange: (Boolean) -> Unit) {
        database.child(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.children.count()
                onDataChange(count > 0)
            }

            override fun onCancelled(error: DatabaseError) {
                onDataChange(false)
            }
        })
    }

    // List o'qish (bir marta)
    fun <T> readList(path: String, clazz: Class<T>, onComplete: (ArrayList<T>?) -> Unit) {
        database.child(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<T>()
                for (child in snapshot.children) {
                    val item = child.getValue(clazz)
                    if (item != null) {
                        list.add(item)
                    }
                }
                onComplete(list)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(null)
            }
        })
    }

    // Real vaqtda ma'lumot qabul qilish //
    fun <T> observeList(path: String, clazz: Class<T>, onDataChange: (ArrayList<T>?) -> Unit) {
        database.child(path).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<T>()
                for (child in snapshot.children) {
                    val item = child.getValue(clazz)
                    if (item != null) {
                        list.add(item)
                    }
                }
                onDataChange(list)
            }

            override fun onCancelled(error: DatabaseError) {
                onDataChange(null)
            }
        })
    }

    fun getChildCount(path: String,onDataChange: (count : Long) -> Unit) {
        database.child(path).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onDataChange(snapshot.childrenCount)
            }

            override fun onCancelled(error: DatabaseError) {
                onDataChange(0)
            }
        })
    }
}
