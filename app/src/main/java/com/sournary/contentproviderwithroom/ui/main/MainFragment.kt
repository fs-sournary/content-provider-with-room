package com.sournary.contentproviderwithroom.ui.main

import android.database.Cursor
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.sournary.contentproviderwithroom.R
import com.sournary.contentproviderwithroom.data.model.Cheese
import com.sournary.contentproviderwithroom.data.source.provider.CheeseProvider
import com.sournary.contentproviderwithroom.utils.setupSupportActionBar
import com.sournary.contentproviderwithroom.utils.showSnackBar

/**
 * Created: 04/09/2018
 * By: Sang
 * Description:
 */
class MainFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var cheeseAdapter: CheeseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_main, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
        setupCheeseList()
        setupLoader()
    }

    private fun setupToolbar() {
        val toolbar = rootView.findViewById<Toolbar>(R.id.toolbar)
        setupSupportActionBar(toolbar) { title = "Cheese List" }
    }

    private fun setupCheeseList() {
        val cheeseRecycler = rootView.findViewById<RecyclerView>(R.id.recycler_cheese)
        cheeseAdapter = CheeseAdapter()
        cheeseRecycler.adapter = cheeseAdapter
        cheeseRecycler.addItemDecoration(
            DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL)
        )
    }

    private fun setupLoader() {
        val progressLoading = rootView.findViewById<ProgressBar>(R.id.progress)
        val loaderCallback = object : LoaderManager.LoaderCallbacks<Cursor> {

            override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
                progressLoading.visibility = View.VISIBLE
                return when (id) {
                    ID_LOADER_MANAGER -> CursorLoader(
                        activity!!.applicationContext,
                        CheeseProvider.URI_CHEESE,
                        Array(1) { Cheese.COLUMN_NAME },
                        null,
                        null,
                        null
                    )
                    else -> throw IllegalArgumentException("Unknown Loader with id: $id")
                }
            }

            override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                progressLoading.visibility = View.GONE
                cheeseAdapter.submitList(cheeseAdapter.getCheeseList(data))
            }

            override fun onLoaderReset(loader: Loader<Cursor>) {}
        }
        LoaderManager.getInstance(this).initLoader(ID_LOADER_MANAGER, null, loaderCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main_option, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {

                true
            }
            R.id.action_search -> {
                showSnackBar("Searching has been clicked")
                true
            }
            else -> false
        }

    companion object {

        private const val ID_LOADER_MANAGER = 0
    }
}
