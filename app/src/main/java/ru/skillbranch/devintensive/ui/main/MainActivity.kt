package ru.skillbranch.devintensive.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.ui.archive.ArchiveActivity
import ru.skillbranch.devintensive.ui.group.GroupActivity
import ru.skillbranch.devintensive.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()
        initViews()
        initViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Введите название чата"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.handleSearchQuery(newText)
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun initViews() {
        chatAdapter = ChatAdapter{ item ->
            when(item.chatType) {
                ChatType.ARCHIVE -> {
                    val intent = Intent(this, ArchiveActivity::class .java)
                    startActivity(intent)
                }
                else -> {
                    val snackbar = Snackbar.make(rv_chat_list, "Click on ${item.title}", Snackbar.LENGTH_LONG)
                    val snackBarView = snackbar.view
                    snackBarView.background = getDrawable(R.drawable.bg_snackbar)
                    val textView: TextView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text)
                    textView.setTextColor(getSnackBarColor())
                    snackbar.show()
                }
            }
        }
        //ДЗ кастом материал декоратор time: 1:13 tutorial 5
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        getDrawable(R.drawable.divider)?.let { divider.setDrawable(it) }
        val touchCallback = ChatItemTouchHelperCallback(chatAdapter) {
            viewModel.addToArchive(it.id)

            //ДЗ добавить обработчик отмены добавления time: 1:33 tutorial 5
            val snackbar = Snackbar.make(rv_chat_list, "Вы точно хотите добавить ${it.title} в архив?", Snackbar.LENGTH_LONG)
                .setAction("отмена".toUpperCase()) { _ -> viewModel.restoreFromArchive(it.id) }
            snackbar.setActionTextColor(getAccentColor())
            val snackBarView = snackbar.view
            snackBarView.background = getDrawable(R.drawable.bg_snackbar)
            val textView: TextView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.setTextColor(getSnackBarColor())
            snackbar.show()
        }
        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_chat_list)

        with(rv_chat_list) {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(divider)
        }

        fab.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getChatData().observe(this, { chatAdapter.updateData(it) })
    }

    private fun getAccentColor(): Int {
        val tv = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, tv, true)
        return tv.data
    }
    private fun getSnackBarColor(): Int {
        val tv = TypedValue()
        theme.resolveAttribute(R.attr.colorSnackBarText, tv, true)
        return tv.data
    }
}