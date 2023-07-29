package codeblock.app.e_news

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.text.method.LinkMovementMethod

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Donate : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_donate, container, false)
        val siteFFPH = view.findViewById<TextView>(R.id.orgWebsite1)
        val siteHaribon = view.findViewById<TextView>(R.id.orgWebsite2)
        val siteGPPH= view.findViewById<TextView>(R.id.orgWebsite3)

        siteFFPH.autoLinkMask = android.text.util.Linkify.WEB_URLS
        siteHaribon.autoLinkMask = android.text.util.Linkify.WEB_URLS
        siteGPPH.autoLinkMask = android.text.util.Linkify.WEB_URLS

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Donate().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
